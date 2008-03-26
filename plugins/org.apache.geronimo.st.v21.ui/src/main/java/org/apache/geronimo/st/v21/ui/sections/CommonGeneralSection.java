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
package org.apache.geronimo.st.v21.ui.sections;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.geronimo.jee.web.WebApp;
import org.apache.geronimo.jee.deployment.Artifact;
import org.apache.geronimo.jee.deployment.Dependencies;
import org.apache.geronimo.jee.deployment.Dependency;
import org.apache.geronimo.jee.deployment.Environment;
import org.apache.geronimo.jee.deployment.ObjectFactory;
import org.apache.geronimo.jee.connector.Connector;
import org.apache.geronimo.st.ui.CommonMessages;
import org.apache.geronimo.st.ui.sections.AbstractSectionPart;
import org.apache.geronimo.st.v21.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public abstract class CommonGeneralSection extends AbstractSectionPart {

	protected Text artifactId;

	protected Text groupId;

	protected Text version;

	protected Text type;

	protected Button inverseClassLoading;

	protected Button suppressDefaultEnv;
	
	protected Button sharedLibDepends;
    
	protected ObjectFactory deploymentObjectFactory = null;

	public CommonGeneralSection(Composite parent, FormToolkit toolkit, int style, JAXBElement plan) {
		super(parent, toolkit, style, plan);
	}

	protected void createClient() {

		Section section = getSection();

		section.setText(CommonMessages.editorSectionGeneralTitle);
		section.setDescription(CommonMessages.editorSectionGeneralDescription);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		section.setClient(composite);

		createLabel(composite, Messages.groupId);

		groupId = toolkit.createText(composite, getGroupId(), SWT.BORDER);
		groupId.setLayoutData(createTextFieldGridData());
		groupId.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getModuleId(true).setGroupId(groupId.getText());
				markDirty();
			}
		});

		createLabel(composite, Messages.artifactId);

		artifactId = toolkit.createText(composite, getArtifactId(), SWT.BORDER);
		artifactId.setLayoutData(createTextFieldGridData());
		artifactId.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getModuleId(true).setArtifactId(artifactId.getText());
				markDirty();
			}
		});

		createLabel(composite, Messages.version);

		version = toolkit.createText(composite, getVersion(), SWT.BORDER);
		version.setLayoutData(createTextFieldGridData());
		version.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getModuleId(true).setVersion(version.getText());
				markDirty();
			}
		});

		createLabel(composite, Messages.artifactType);

		type = toolkit.createText(composite, getArtifact(), SWT.BORDER);
		type.setLayoutData(createTextFieldGridData());
		type.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getModuleId(true).setType(type.getText());
				markDirty();
			}
		});

		inverseClassLoading = toolkit.createButton(composite, Messages.inverseClassloading, SWT.CHECK);
		inverseClassLoading.setSelection(isInverseClassloading());
		GridData data = new GridData();
		data.horizontalSpan = 2;
		inverseClassLoading.setLayoutData(data);

		inverseClassLoading.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				setInverseClassloading(inverseClassLoading.getSelection());
				markDirty();
			}
		});

		suppressDefaultEnv = toolkit.createButton(composite, Messages.supressDefaultEnv, SWT.CHECK);
		suppressDefaultEnv.setSelection(isSuppressDefaultEnvironment());
		data = new GridData();
		data.horizontalSpan = 2;
		suppressDefaultEnv.setLayoutData(data);

		suppressDefaultEnv.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				setSuppressDefaultEnvironment(suppressDefaultEnv.getSelection());
				markDirty();
			}
		});
		
		sharedLibDepends = toolkit.createButton(composite, Messages.sharedLibDepends, SWT.CHECK);
		sharedLibDepends.setSelection(isSharedLibDependency());
		data = new GridData();
		data.horizontalSpan = 2;
		sharedLibDepends.setLayoutData(data);

		sharedLibDepends.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				setSharedLibDependency(sharedLibDepends.getSelection());
				markDirty();
			}
		});
	}

	protected Label createLabel(Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		return label;
	}

	protected GridData createTextFieldGridData() {
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.widthHint = 150;
		return data;
	}

	protected String getGroupId() {
		Artifact moduleId = getModuleId(false);
		if (moduleId != null
				&& moduleId.getGroupId() != null)
			return moduleId.getGroupId();
		return "";
	}

	protected String getArtifactId() {
		Artifact moduleId = getModuleId(false);
		if (moduleId != null
				&& moduleId.getArtifactId() != null)
			return moduleId.getArtifactId();
		return "";
	}

	protected String getVersion() {
		Artifact moduleId = getModuleId(false);
		if (moduleId != null
				&& moduleId.getVersion() != null)
			return moduleId.getVersion();
		return "";
	}

	protected String getArtifact() {
		Artifact moduleId = getModuleId(false);
		if (moduleId != null
				&& moduleId.getType() != null)
			return moduleId.getType();
		return "";
	}

	protected boolean isInverseClassloading() {
		Environment type = getEnvironment(false);
		return type != null && type.getInverseClassloading() != null;
	}

	protected boolean isSuppressDefaultEnvironment() {
		Environment type = getEnvironment(false);
		return type != null && type.getSuppressDefaultEnvironment() != null;
	}
	
	protected boolean isSharedLibDependency() {
		Dependencies depType = getDependencies(false);
		if(depType != null) {
			return getSharedLibDependency(depType) != null;
		}
		return false;
	}

	protected void setInverseClassloading(boolean enable) {
		if (enable) {
			Environment type = getEnvironment(true);
			type.setInverseClassloading(getDeploymentObjectFactory().createEmpty());
		} else {
			Environment type = getEnvironment(false);
			if (type != null) {
				type.setInverseClassloading(null);
			}
		}
	}

	protected void setSuppressDefaultEnvironment(boolean enable) {
		if (enable) {
			Environment type = getEnvironment(true);
			type.setSuppressDefaultEnvironment(getDeploymentObjectFactory().createEmpty());
		} else {
			Environment type = getEnvironment(false);
			if (type != null) {
				type.setSuppressDefaultEnvironment(null);
			}
		}
	}
	
	protected void setSharedLibDependency(boolean enable) {
		if (enable) {
			Dependencies deptype = getDependencies(true);
			Dependency sharedLib = getDeploymentObjectFactory().createDependency();
			sharedLib.setGroupId("org.apache.geronimo.configs");
			sharedLib.setArtifactId("sharedlib");
			sharedLib.setType("car");
			deptype.getDependency().add(sharedLib);
		} else {
			Dependencies deptype = getDependencies(false);
			if (deptype != null) {
				Artifact artifact = getSharedLibDependency(deptype);
				if(artifact != null) {
					deptype.getDependency().remove(artifact);
				}
			}
		}
	}
	
	private Artifact getSharedLibDependency(Dependencies dependencies) {
		Dependencies depType = getDependencies(false);
		List dependenciesList = depType.getDependency();
		Iterator i = dependenciesList.iterator();
		while(i.hasNext()) {
			Artifact artifact = (Artifact) i.next();
			if("org.apache.geronimo.configs".equals(artifact.getGroupId()) && "sharedlib".equals(artifact.getArtifactId()) && "car".equals(artifact.getType())) {
				return artifact;
			}
		}
		return null;
	}


	//
	// Need to support both WebApp and Connector 
	//
	private Environment getEnvironment(boolean create) {
		
		Environment type = null;
		Object plan = getPlan().getValue();
		if ( WebApp.class.isInstance( plan ) ) {
			type = ((WebApp)plan).getEnvironment();
			if (type == null && create) {
                type = getDeploymentObjectFactory().createEnvironment();
                ((WebApp)plan).setEnvironment( type );
			}
		}
		if ( Connector.class.isInstance( plan ) ) {
			type = ((Connector)plan).getEnvironment();
			if (type == null && create) {
                type = getDeploymentObjectFactory().createEnvironment();
                ((Connector)plan).setEnvironment( type );
			}
		}
        return type;
		
	}
	
	private Dependencies getDependencies(boolean create) {
		Environment env = getEnvironment(create);
		if(env != null) {
			Dependencies dep = env.getDependencies();
			if (dep == null && create) {
				dep = getDeploymentObjectFactory().createDependencies();
				env.setDependencies(dep);
			}
			return dep;
		}
		return null;
	}

	private Artifact getModuleId(boolean create) {
		Environment type = getEnvironment(create);
		if (type != null) {
			Artifact moduleId = type.getModuleId();
			if (moduleId == null && create) {
				moduleId = getDeploymentObjectFactory().createArtifact();
				type.setModuleId(moduleId);
			}
			return moduleId;
		}
		return null;
	}
	
	private org.apache.geronimo.jee.deployment.ObjectFactory getDeploymentObjectFactory() {
		if ( deploymentObjectFactory == null ) {
			deploymentObjectFactory = new org.apache.geronimo.jee.deployment.ObjectFactory();
		}
		return deploymentObjectFactory;
	}

	protected abstract JAXBElement getEnvironmentEReference();

}
