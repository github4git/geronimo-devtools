/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geronimo.st.v1.core;

import java.net.URI;
import java.net.URL;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.directory.NoSuchAttributeException;

import org.apache.geronimo.gbean.GBeanData;
import org.apache.geronimo.gbean.GBeanQuery;
import org.apache.geronimo.kernel.GBeanNotFoundException;
import org.apache.geronimo.kernel.InternalKernelException;
import org.apache.geronimo.kernel.Kernel;
import org.apache.geronimo.kernel.config.Configuration;
import org.apache.geronimo.kernel.config.PersistentConfigurationList;
import org.apache.geronimo.kernel.jmx.KernelDelegate;
import org.apache.geronimo.st.core.GeronimoServerBehaviourDelegate;
import org.apache.geronimo.st.v1.core.internal.Trace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.IModulePublishHelper;

/**
 * @version $Rev$ $Date$
 */
public class GeronimoServerBehaviour extends GeronimoServerBehaviourDelegate implements IModulePublishHelper {

	private Kernel kernel = null;

	public GeronimoServerBehaviour() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.apache.geronimo.st.core.GeronimoServerBehaviourDelegate#stopKernel()
	 */
	protected void stopKernel() {
		if (kernel != null) {
			kernel.shutdown();
			kernel = null;
		}
	}

	/**
	 * @return
	 * @throws SecurityException
	 */
	protected Kernel getKernel() throws SecurityException {
		if (kernel == null) {
			try {
				MBeanServerConnection connection = getServerConnection();
				if (connection != null)
					kernel = new KernelDelegate(connection);
			} catch (SecurityException e) {
				throw e;
			} catch (Exception e) {
				Trace.trace(Trace.WARNING, "Kernel connection failed. " + e.getMessage());
			}
		}
		return kernel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.st.core.IGeronimoServerBehavior#isKernelAlive()
	 */
	public boolean isKernelAlive() {
		try {
			return getKernel() != null && kernel.isRunning();
		} catch (SecurityException e) {
			Activator.log(Status.ERROR, "Invalid username and/or password.", e);
			pingThread.interrupt();
			if (getServer().getServerState() != IServer.STATE_STOPPED) {
				stop(true);
			}
		} catch (Exception e) {
			Activator.log(Status.WARNING, "Geronimo Server may have been terminated manually outside of workspace.", e);
			kernel = null;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.st.core.IGeronimoServerBehavior#isFullyStarted()
	 */
	public boolean isFullyStarted() {
		if (isKernelAlive()) {
			Set configLists = kernel.listGBeans(new GBeanQuery(null, PersistentConfigurationList.class.getName()));
			if (!configLists.isEmpty()) {
				ObjectName on = (ObjectName) configLists.toArray()[0];
				try {
					Boolean b = (Boolean) kernel.getAttribute(on, "kernelFullyStarted");
					return b.booleanValue();
				} catch (GBeanNotFoundException e) {
					// ignore
				} catch (NoSuchAttributeException e) {
					// ignore
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Trace.trace(Trace.INFO, "configLists is empty");
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.internal.IModulePublishHelper#getPublishDirectory(org.eclipse.wst.server.core.IModule[])
	 */
	public IPath getPublishDirectory(IModule[] module) {
		if (module == null || module.length == 0)
			return null;
		
		ClassLoader old = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getContextClassLoader());
			String configId = getConfigId(module[0]);
			ObjectName on = Configuration.getConfigurationObjectName(URI.create(configId));
			GBeanData data = kernel.getGBeanData(on);
			URL url = (URL) data.getAttribute("baseURL");
			return getModulePath(module, url);
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (GBeanNotFoundException e) {
			e.printStackTrace();
		} catch (InternalKernelException e) {
			e.printStackTrace();
		} finally {
			Thread.currentThread().setContextClassLoader(old);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.st.core.GenericGeronimoServerBehaviour#getContextClassLoader()
	 */
	protected ClassLoader getContextClassLoader() {
		return Kernel.class.getClassLoader();
	}
}