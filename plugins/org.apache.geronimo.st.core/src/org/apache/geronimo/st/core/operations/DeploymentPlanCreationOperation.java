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
package org.apache.geronimo.st.core.operations;

import java.io.IOException;
import java.util.Collections;

import org.apache.geronimo.st.core.GeronimoUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @version $Rev$ $Date$
 */
public abstract class DeploymentPlanCreationOperation extends
		AbstractGeronimoJ2EEComponentOperation implements
		IDeploymentPlanCreationOp {
	
	protected Object config;

	public DeploymentPlanCreationOperation(IDataModel model, Object config) {
		super(model);
		this.config = config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.core.runtime.IAdaptable)
	 */
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		execute();
		return Status.OK_STATUS;
	}

	public void execute() {
		IVirtualComponent comp = ComponentCore.createComponent(getProject());

		String type = J2EEProjectUtilities.getJ2EEProjectType(getProject());

		if (IModuleConstants.JST_WEB_MODULE.equals(type)) {
			createGeronimoWebDeploymentPlan(GeronimoUtils.getWebDeploymentPlanFile(comp));
		} else if (IModuleConstants.JST_EJB_MODULE.equals(type)) {
			createOpenEjbDeploymentPlan(GeronimoUtils.getOpenEjbDeploymentPlanFile(comp));
		} else if (IModuleConstants.JST_EAR_MODULE.equals(type)) {
			createGeronimoApplicationDeploymentPlan(GeronimoUtils.getApplicationDeploymentPlanFile(comp));
		} else if (IModuleConstants.JST_CONNECTOR_MODULE.equals(type)) {
			createConnectorDeploymentPlan(GeronimoUtils.getConnectorDeploymentPlanFile(comp));
		} else if (IModuleConstants.JST_UTILITY_MODULE.equals(type)) {
			createServiceDeploymentPlan(GeronimoUtils.getServiceDeploymentPlanFile(comp));
		}
	}

	public void save(Resource resource) {

		if (resource instanceof XMLResource) {
			((XMLResource) resource).setEncoding("UTF-8");
		}

		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public EObject createOpenEjbDeploymentPlan(IFile file) {
		return null;
	}

	public EObject createGeronimoWebDeploymentPlan(IFile file) {
		return null;
	}

	public EObject createGeronimoApplicationDeploymentPlan(IFile file) {
		return null;
	}

	public EObject createConnectorDeploymentPlan(IFile file) {
		return null;
	}
	
	public EObject createServiceDeploymentPlan(IFile file) {
		return null;
	}
}
