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
package org.apache.geronimo.st.ui.editors;

import java.util.HashMap;
import java.util.Map;

import org.apache.geronimo.st.ui.Activator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jst.server.core.FacetUtil;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @version $Rev$ $Date$
 */
public class SharedDeploymentPlanEditor extends AbstractGeronimoDeploymentPlanEditor {

	private static Map loaders = new HashMap();

	private IGeronimoFormContentLoader currentLoader = null;

	static {
		loadExtensionPoints();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.st.ui.editors.AbstractGeronimoDeploymentPlanEditor#doAddPages()
	 */
	public void doAddPages() throws PartInitException {
		if (getDeploymentPlan() != null && getLoader() != null) {
			currentLoader.doAddPages(this);
		}
		addSourcePage();
	}

	private static synchronized void loadExtensionPoints() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(Activator.PLUGIN_ID, "loader");
		for (int i = 0; i < cf.length; i++) {
			IConfigurationElement element = cf[i];
			if ("loader".equals(element.getName())) {
				try {
					IGeronimoFormContentLoader loader = (IGeronimoFormContentLoader) element.createExecutableExtension("class");
					String version = element.getAttribute("version");
					loaders.put(version, loader);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.st.ui.editors.AbstractGeronimoDeploymentPlanEditor#loadDeploymentPlan(org.eclipse.core.resources.IFile)
	 */
	public EObject loadDeploymentPlan(IFile file) {
		return getLoader() != null ? currentLoader.loadDeploymentPlan(file)
				: null;
	}

	private IGeronimoFormContentLoader getLoader() {
		if (currentLoader == null) {
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				IProject project = ((IFileEditorInput) input).getFile().getProject();
				try {
					IFacetedProject fp = ProjectFacetsManager.create(project);
					IRuntime runtime = FacetUtil.getRuntime(fp.getPrimaryRuntime());
					String version = runtime.getRuntimeType().getVersion();
					currentLoader = (IGeronimoFormContentLoader) loaders.get(version);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return currentLoader;
	}

}
