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
package org.apache.geronimo.st.v1.core.operations;

import org.apache.geronimo.st.core.GeronimoSchemaNS;
import org.apache.geronimo.st.core.operations.DeploymentPlanCreationOperation;
import org.apache.geronimo.st.v1.core.GeronimoV1Utils;
import org.apache.geronimo.xml.ns.j2ee.application.ApplicationFactory;
import org.apache.geronimo.xml.ns.j2ee.application.ApplicationType;
import org.apache.geronimo.xml.ns.j2ee.connector.ConnectorFactory;
import org.apache.geronimo.xml.ns.j2ee.connector.ConnectorType;
import org.apache.geronimo.xml.ns.j2ee.web.WebAppType;
import org.apache.geronimo.xml.ns.j2ee.web.WebFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.openejb.xml.ns.openejb.jar.JarFactory;
import org.openejb.xml.ns.openejb.jar.OpenejbJarType;

/**
 * @version $Rev$ $Date$
 */
public class V1DeploymentPlanCreationOperation extends
		DeploymentPlanCreationOperation {

	public V1DeploymentPlanCreationOperation(IDataModel model, Object config) {
		super(model, config);
	}

	/* (non-Javadoc)
	 * @see org.apache.geronimo.st.core.operations.IDeploymentPlanCreationOp#createGeronimoApplicationDeploymentPlan(org.eclipse.core.resources.IFile)
	 */
	public EObject createGeronimoApplicationDeploymentPlan(IFile dpFile) {
		URI uri = URI.createPlatformResourceURI(dpFile.getFullPath().toString());

		ResourceSet resourceSet = new ResourceSetImpl();
		GeronimoV1Utils.registerAppFactoryAndPackage(resourceSet);

		Resource resource = resourceSet.createResource(uri);
		org.apache.geronimo.xml.ns.j2ee.application.DocumentRoot documentRoot = ApplicationFactory.eINSTANCE.createDocumentRoot();
		ApplicationType root = ApplicationFactory.eINSTANCE.createApplicationType();

		EMap map = documentRoot.getXMLNSPrefixMap();
		map.put("", GeronimoSchemaNS.GERONIMO_APP_NS_1_0);
		map.put("sec", GeronimoSchemaNS.GERONIMO_SECURITY_NS_1_1);
		map.put("sys", GeronimoSchemaNS.GERONIMO_DEPLOYMENT_NS_1_0);

		String projectName = getProject().getName();
		root.setApplicationName(getProject().getName());
		root.setConfigId(projectName + "/" + projectName);

		documentRoot.setApplication(root);
		resource.getContents().add(documentRoot);

		save(resource);

		return root;
	}

	/* (non-Javadoc)
	 * @see org.apache.geronimo.st.core.operations.IDeploymentPlanCreationOp#createGeronimoWebDeploymentPlan(org.eclipse.core.resources.IFile)
	 */
	public EObject createGeronimoWebDeploymentPlan(IFile dpFile) {

		URI uri = URI.createPlatformResourceURI(dpFile.getFullPath().toString());

		ResourceSet resourceSet = new ResourceSetImpl();
		GeronimoV1Utils.registerWebFactoryAndPackage(resourceSet);

		Resource resource = resourceSet.createResource(uri);
		org.apache.geronimo.xml.ns.j2ee.web.DocumentRoot documentRoot = WebFactory.eINSTANCE.createDocumentRoot();

		EMap map = documentRoot.getXMLNSPrefixMap();
		map.put("", GeronimoSchemaNS.GERONIMO_WEB_NS_1_0);
		map.put("sec", GeronimoSchemaNS.GERONIMO_SECURITY_NS_1_1);
		map.put("nam", GeronimoSchemaNS.GERONIMO_NAMING_NS_1_0);
		map.put("sys", GeronimoSchemaNS.GERONIMO_DEPLOYMENT_NS_1_0);

		WebAppType root = WebFactory.eINSTANCE.createWebAppType();

		String projectName = getProject().getName();
		root.setConfigId(projectName + "/" + projectName);
		root.setContextRoot("/" + projectName);
		root.setContextPriorityClassloader(false);

		documentRoot.setWebApp(root);
		resource.getContents().add(documentRoot);

		save(resource);

		return root;
	}

	/* (non-Javadoc)
	 * @see org.apache.geronimo.st.core.operations.IDeploymentPlanCreationOp#createOpenEjbDeploymentPlan(org.eclipse.core.resources.IFile)
	 */
	public EObject createOpenEjbDeploymentPlan(IFile dpFile) {
		URI uri = URI.createPlatformResourceURI(dpFile.getFullPath().toString());

		ResourceSet resourceSet = new ResourceSetImpl();
		GeronimoV1Utils.registerEjbFactoryAndPackage(resourceSet);

		Resource resource = resourceSet.createResource(uri);
		org.openejb.xml.ns.openejb.jar.DocumentRoot documentRoot = JarFactory.eINSTANCE.createDocumentRoot();
		OpenejbJarType root = JarFactory.eINSTANCE.createOpenejbJarType();

		EMap map = documentRoot.getXMLNSPrefixMap();
		map.put("", GeronimoSchemaNS.GERONIMO_OPENEJB_NS_2_0);
		map.put("sec", GeronimoSchemaNS.GERONIMO_SECURITY_NS_1_1);
		map.put("nam", GeronimoSchemaNS.GERONIMO_NAMING_NS_1_0);
		map.put("sys", GeronimoSchemaNS.GERONIMO_DEPLOYMENT_NS_1_0);
		map.put("pkgen", GeronimoSchemaNS.GERONIMO_PKGEN_NS_2_0);

		String projectName = getProject().getName();
		root.setConfigId(projectName + "/" + projectName);

		root.setEnterpriseBeans(JarFactory.eINSTANCE.createEnterpriseBeansType());

		documentRoot.setOpenejbJar(root);
		resource.getContents().add(documentRoot);

		save(resource);

		return root;
	}

	/* (non-Javadoc)
	 * @see org.apache.geronimo.st.core.operations.IDeploymentPlanCreationOp#createConnectorDeploymentPlan(org.eclipse.core.resources.IFile)
	 */
	public EObject createConnectorDeploymentPlan(IFile dpFile) {
		URI uri = URI.createPlatformResourceURI(dpFile.getFullPath().toString());

		ResourceSet resourceSet = new ResourceSetImpl();
		GeronimoV1Utils.registerConnectorFactoryAndPackage(resourceSet);

		Resource resource = resourceSet.createResource(uri);
		org.apache.geronimo.xml.ns.j2ee.connector.DocumentRoot documentRoot = ConnectorFactory.eINSTANCE.createDocumentRoot();
		ConnectorType root = ConnectorFactory.eINSTANCE.createConnectorType();

		EMap map = documentRoot.getXMLNSPrefixMap();
		map.put("", GeronimoSchemaNS.GERONIMO_CONNECTOR_NS_1_0);
		map.put("nam", GeronimoSchemaNS.GERONIMO_NAMING_NS_1_0);
		map.put("sys", GeronimoSchemaNS.GERONIMO_DEPLOYMENT_NS_1_0);

		String projectName = getProject().getName();
		root.setConfigId(projectName + "/" + projectName);

		documentRoot.setConnector(root);
		resource.getContents().add(documentRoot);

		save(resource);

		return root;
	}

}
