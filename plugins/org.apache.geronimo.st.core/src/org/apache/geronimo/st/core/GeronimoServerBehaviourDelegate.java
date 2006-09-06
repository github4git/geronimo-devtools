/**
 * Copyright 2004, 2005 The Apache Software Foundation or its licensors, as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.geronimo.st.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.enterprise.deploy.spi.DeploymentManager;
import javax.enterprise.deploy.spi.Target;
import javax.enterprise.deploy.spi.TargetModuleID;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.geronimo.st.core.commands.DeploymentCmdStatus;
import org.apache.geronimo.st.core.commands.DeploymentCommandFactory;
import org.apache.geronimo.st.core.commands.IDeploymentCommand;
import org.apache.geronimo.st.core.commands.TargetModuleIdNotFoundException;
import org.apache.geronimo.st.core.internal.Messages;
import org.apache.geronimo.st.core.internal.Trace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.util.SocketUtil;

abstract public class GeronimoServerBehaviourDelegate extends ServerBehaviourDelegate implements IGeronimoServerBehavior {

	public static final int TIMER_TASK_INTERVAL = 10;

	protected IProgressMonitor _monitor;

	protected Timer timer = null;

	protected PingThread pingThread;

	protected transient IProcess process;

	protected transient IDebugEventSetListener processListener;

	abstract public String getConfigId(IModule module);

	abstract protected ClassLoader getContextClassLoader();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#setupLaunchConfiguration(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy wc, IProgressMonitor monitor) throws CoreException {
		if (isRemote())// No launch for remote servers.
			return;

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, getRuntimeClass());

		GeronimoRuntimeDelegate runtime = getRuntimeDelegate();

		IVMInstall vmInstall = runtime.getVMInstall();
		if (vmInstall != null)
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, JavaRuntime.newJREContainerPath(vmInstall).toPortableString());

		setupLaunchClasspath(wc, vmInstall);

		String existingProgArgs = wc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (String) null);
		String serverProgArgs = getServerDelegate().getConsoleLogLevel();
		if (existingProgArgs == null
				|| existingProgArgs.indexOf(serverProgArgs) < 0) {
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, serverProgArgs);
		}
	}

	/**
	 * @param launch
	 * @param launchMode
	 * @param monitor
	 * @throws CoreException
	 */
	protected void setupLaunch(ILaunch launch, String launchMode, IProgressMonitor monitor) throws CoreException {
		Trace.trace(Trace.INFO, "--> GeronimoServerBehavior.setupLaunch()");

		if (!SocketUtil.isLocalhost(getServer().getHost()))
			return;

		ServerPort[] ports = getServer().getServerPorts(null);
		for (int i = 0; i < ports.length; i++) {
			ServerPort sp = ports[i];
			if (SocketUtil.isPortInUse(ports[i].getPort(), 5))
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.bind(Messages.errorPortInUse, Integer.toString(sp.getPort()), sp.getName()), null));
		}

		stopUpdateServerStateTask();
		setServerState(IServer.STATE_STARTING);
		setMode(launchMode);

		IServerListener listener = new IServerListener() {
			public void serverChanged(ServerEvent event) {
				int eventKind = event.getKind();
				if ((eventKind & ServerEvent.STATE_CHANGE) != 0) {
					int state = event.getServer().getServerState();
					if (state == IServer.STATE_STARTED
							|| state == IServer.STATE_STOPPED) {
						GeronimoServerBehaviourDelegate.this.getServer().removeServerListener(this);
						startUpdateServerStateTask();
					}
				}
			}
		};

		getServer().addServerListener(listener);
		Trace.trace(Trace.INFO, "<-- GeronimoServerBehavior.setupLaunch()");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#stop(boolean)
	 */
	public void stop(boolean force) {
		if (force) {
			terminate();
			return;
		}
		int state = getServer().getServerState();
		if (state == IServer.STATE_STOPPED)
			return;
		if (state == IServer.STATE_STARTING || state == IServer.STATE_STOPPING)
			terminate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#publishModule(int,
	 *      int, org.eclipse.wst.server.core.IModule[],
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void publishModule(int kind, int deltaKind, IModule[] module, IProgressMonitor monitor) throws CoreException {

		Trace.trace(Trace.INFO, ">> publishModule(), deltaKind = " + deltaKind);
		Trace.trace(Trace.INFO, Arrays.asList(module).toString());
		_monitor = monitor;

		if (module.length == 1 && (deltaKind == ADDED || deltaKind == REMOVED)) {
			invokeCommand(deltaKind, module[0]);
		} else if (deltaKind == CHANGED) {
			// TODO This case is flawed due to WTP Bugzilla 123676
			invokeCommand(deltaKind, module[0]);
		}

		setModulePublishState(module, IServer.PUBLISH_STATE_NONE);

		Trace.trace(Trace.INFO, "<< publishModule()");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#publishFinish(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void publishFinish(IProgressMonitor monitor) throws CoreException {
		IModule[] modules = this.getServer().getModules();
		boolean allpublished = true;
		for (int i = 0; i < modules.length; i++) {
			if (this.getServer().getModulePublishState(new IModule[] { modules[i] }) != IServer.PUBLISH_STATE_NONE)
				allpublished = false;
		}
		if (allpublished)
			setServerPublishState(IServer.PUBLISH_STATE_NONE);

		GeronimoConnectionFactory.getInstance().destroy(getServer());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#initialize(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void initialize(IProgressMonitor monitor) {
		Trace.trace(Trace.INFO, "GeronimoServerBehavior.initialize()");
		startUpdateServerStateTask();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#dispose()
	 */
	public void dispose() {
		stopUpdateServerStateTask();
	}

	public String getRuntimeClass() {
		return "org.apache.geronimo.system.main.Daemon";
	}

	public void setServerStarted() {
		setServerState(IServer.STATE_STARTED);
	}

	public void setServerStopped() {
		setServerState(IServer.STATE_STOPPED);
	}

	public IGeronimoServer getGeronimoServer() {
		return (IGeronimoServer) getServer().loadAdapter(IGeronimoServer.class, null);
	}

	protected void terminate() {
		if (getServer().getServerState() == IServer.STATE_STOPPED)
			return;

		try {
			setServerState(IServer.STATE_STOPPING);
			Trace.trace(Trace.INFO, "Killing the geronimo server process"); //$NON-NLS-1$
			if (process != null && !process.isTerminated()) {
				process.terminate();

			}
			stopImpl();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error killing the geronimo server process", e); //$NON-NLS-1$
		}
	}

	protected void stopImpl() {
		if (pingThread != null) {
			pingThread.interrupt();
			pingThread = null;
		}
		if (process != null) {
			process = null;
			DebugPlugin.getDefault().removeDebugEventListener(processListener);
			processListener = null;
		}
		setServerState(IServer.STATE_STOPPED);
	}

	protected void invokeCommand(int deltaKind, IModule module) throws CoreException {
		ClassLoader old = Thread.currentThread().getContextClassLoader();
		try {
			ClassLoader cl = getContextClassLoader();
			if (cl != null)
				Thread.currentThread().setContextClassLoader(cl);
			switch (deltaKind) {
			case ADDED: {
				doDeploy(module);
				break;
			}
			case CHANGED: {
				doRedeploy(module);
				break;
			}
			case REMOVED: {
				doUndeploy(module);
				break;
			}
			default:
				throw new IllegalArgumentException();
			}
		} catch (CoreException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Thread.currentThread().setContextClassLoader(old);
		}
	}

	protected void doDeploy(IModule module) throws Exception {
		Trace.trace(Trace.INFO, ">> doDeploy() " + module.toString());

		DeploymentManager dm = DeploymentCommandFactory.getDeploymentManager(getServer());

		if (!DeploymentUtils.configurationExists(module, dm)) {
			IStatus status = distribute(module);
			if (!status.isOK()) {
				doFail(status, Messages.DISTRIBUTE_FAIL);
			}

			TargetModuleID[] ids = ((DeploymentCmdStatus) status).getResultTargetModuleIDs();
			ModuleArtifactMapper mapper = ModuleArtifactMapper.getInstance();
			mapper.addEntry(getServer(), module.getProject(), ids[0].getModuleID());

			status = start(module);
			if (!status.isOK()) {
				doFail(status, Messages.START_FAIL);
			}
		} else {
			String id = getConfigId(module);
			String message = id
					+ "already exists.  Existing configuration will be overwritten.";
			Activator.log(Status.ERROR, message, null);
			doRedeploy(module);
		}

		Trace.trace(Trace.INFO, "<< doDeploy() " + module.toString());
	}

	protected void doRedeploy(IModule module) throws Exception {
		Trace.trace(Trace.INFO, ">> doRedeploy() " + module.toString());

		try {
			IStatus status = reDeploy(module);
			if (!status.isOK()) {
				doFail(status, Messages.REDEPLOY_FAIL);
			}
		} catch (TargetModuleIdNotFoundException e) {
			Activator.log(Status.WARNING, "Module may have been uninstalled outside the workspace.", e);
			doDeploy(module);
		}

		Trace.trace(Trace.INFO, "<< doRedeploy() " + module.toString());
	}

	protected void doUndeploy(IModule module) throws Exception {
		Trace.trace(Trace.INFO, ">> doUndeploy() " + module.toString());

		IStatus status = stop(module);
		if (!status.isOK()) {
			doFail(status, Messages.STOP_FAIL);
		}

		status = unDeploy(module);
		if (!status.isOK()) {
			doFail(status, Messages.UNDEPLOY_FAIL);
		}

		Trace.trace(Trace.INFO, "<< doUndeploy()" + module.toString());
	}

	protected void doRestart(IModule module) throws Exception {
		Trace.trace(Trace.INFO, ">> doRestart() " + module.toString());

		IStatus status = stop(module);
		if (!status.isOK()) {
			doFail(status, Messages.STOP_FAIL);
		}

		status = start(module);
		if (!status.isOK()) {
			doFail(status, Messages.START_FAIL);
		}

		Trace.trace(Trace.INFO, ">> doRestart() " + module.toString());
	}

	protected void doFail(IStatus status, String message) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, message, new Exception(status.getMessage())));
	}

	protected IStatus distribute(IModule module) throws Exception {
		IDeploymentCommand cmd = DeploymentCommandFactory.createDistributeCommand(module, getServer());
		return cmd.execute(_monitor);
	}

	protected IStatus start(IModule module) throws Exception {
		TargetModuleID id = DeploymentUtils.getTargetModuleID(module, DeploymentCommandFactory.getDeploymentManager(getServer()));
		IDeploymentCommand cmd = DeploymentCommandFactory.createStartCommand(new TargetModuleID[] { id }, module, getServer());
		return cmd.execute(_monitor);
	}

	protected IStatus stop(IModule module) throws Exception {
		IDeploymentCommand cmd = DeploymentCommandFactory.createStopCommand(module, getServer());
		return cmd.execute(_monitor);
	}

	protected IStatus unDeploy(IModule module) throws Exception {
		IDeploymentCommand cmd = DeploymentCommandFactory.createUndeployCommand(module, getServer());
		return cmd.execute(_monitor);
	}

	protected IStatus reDeploy(IModule module) throws Exception {
		IDeploymentCommand cmd = DeploymentCommandFactory.createRedeployCommand(module, getServer());
		return cmd.execute(_monitor);
	}

	public Map getServerInstanceProperties() {
		return getRuntimeDelegate().getServerInstanceProperties();
	}

	protected GeronimoRuntimeDelegate getRuntimeDelegate() {
		GeronimoRuntimeDelegate rd = (GeronimoRuntimeDelegate) getServer().getRuntime().getAdapter(GeronimoRuntimeDelegate.class);
		if (rd == null)
			rd = (GeronimoRuntimeDelegate) getServer().getRuntime().loadAdapter(GeronimoRuntimeDelegate.class, new NullProgressMonitor());
		return rd;
	}

	protected GeronimoServerDelegate getServerDelegate() {
		GeronimoServerDelegate sd = (GeronimoServerDelegate) getServer().getAdapter(GeronimoServerDelegate.class);
		if (sd == null)
			sd = (GeronimoServerDelegate) getServer().loadAdapter(GeronimoServerDelegate.class, new NullProgressMonitor());
		return sd;
	}

	protected boolean isRemote() {
		return getServer().getServerType().supportsRemoteHosts()
				&& !SocketUtil.isLocalhost(getServer().getHost());
	}

	protected void setupLaunchClasspath(ILaunchConfigurationWorkingCopy wc, IVMInstall vmInstall) throws CoreException {
		List cp = new ArrayList();
		IPath serverJar = getServer().getRuntime().getLocation().append("/bin/server.jar");
		cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(serverJar));
		// merge existing classpath with server classpath
		IRuntimeClasspathEntry[] existingCps = JavaRuntime.computeUnresolvedRuntimeClasspath(wc);
		for (int i = 0; i < existingCps.length; i++) {
			if (cp.contains(existingCps[i]) == false) {
				cp.add(existingCps[i]);
			}
		}
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, convertCPEntryToMemento(cp));
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
	}

	private List convertCPEntryToMemento(List cpEntryList) {
		List list = new ArrayList(cpEntryList.size());
		Iterator iterator = cpEntryList.iterator();
		while (iterator.hasNext()) {
			IRuntimeClasspathEntry entry = (IRuntimeClasspathEntry) iterator.next();
			try {
				list.add(entry.getMemento());
			} catch (CoreException e) {
				Trace.trace(Trace.SEVERE, "Could not resolve classpath entry: "
						+ entry, e);
			}
		}
		return list;
	}

	public void setProcess(final IProcess newProcess) {
		if (process != null)
			return;

		process = newProcess;
		if (processListener != null)
			DebugPlugin.getDefault().removeDebugEventListener(processListener);
		if (newProcess == null)
			return;

		processListener = new IDebugEventSetListener() {
			public void handleDebugEvents(DebugEvent[] events) {
				if (events != null) {
					int size = events.length;
					for (int i = 0; i < size; i++) {
						if (process != null
								&& process.equals(events[i].getSource())
								&& events[i].getKind() == DebugEvent.TERMINATE) {
							DebugPlugin.getDefault().removeDebugEventListener(this);
							stopImpl();
						}
					}
				}
			}
		};
		DebugPlugin.getDefault().addDebugEventListener(processListener);
	}

	protected void startPingThread() {
		pingThread = new PingThread(this, getServer());
		pingThread.start();
	}

	protected void startUpdateServerStateTask() {
		Trace.trace(Trace.INFO, "startUpdateServerStateTask() "
				+ getServer().getName());
		timer = new Timer(true);
		timer.schedule(new UpdateServerStateTask(this, getServer()), 10000, TIMER_TASK_INTERVAL * 1000);
	}

	protected void stopUpdateServerStateTask() {
		Trace.trace(Trace.INFO, "stopUpdateServerStateTask() "
				+ getServer().getName());
		if (timer != null)
			timer.cancel();
	}

	protected IPath getModulePath(IModule[] module, URL baseURL) {
		IPath modulePath = new Path(baseURL.getFile());

		if (module.length == 2) {
			IModule workingModule = module[module.length - 1];
			modulePath = modulePath.append(workingModule.getName());
			if (GeronimoUtils.isWebModule(workingModule)) {
				modulePath = modulePath.addFileExtension("war");
			} else if (GeronimoUtils.isEjbJarModule(workingModule)) {
				modulePath = modulePath.addFileExtension("jar");
			} else if (GeronimoUtils.isRARModule(workingModule)) {
				modulePath = modulePath.addFileExtension("rar");
			} else if (GeronimoUtils.isEarModule(workingModule)) {
				modulePath = modulePath.addFileExtension("ear");
			}
		}

		return modulePath;
	}

	protected MBeanServerConnection getServerConnection() throws Exception {
		Map map = new HashMap();
		String user = getGeronimoServer().getAdminID();
		String password = getGeronimoServer().getAdminPassword();
		map.put("jmx.remote.credentials", new String[] { user, password });
		map.put("java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory");
		map.put("java.naming.factory.url.pkgs", "org.apache.geronimo.naming");
		map.put("java.naming.provider.url", "rmi://" + getServer().getHost()
				+ ":1099");

		String url = getGeronimoServer().getJMXServiceURL();
		if (url != null) {
			try {
				JMXServiceURL address = new JMXServiceURL(url);
				JMXConnector jmxConnector = JMXConnectorFactory.connect(address, map);
				MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
				Trace.trace(Trace.INFO, "Connected to kernel. " + url);
				return connection;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	
	public Target[] getTargets() {
		return null;
	}
}