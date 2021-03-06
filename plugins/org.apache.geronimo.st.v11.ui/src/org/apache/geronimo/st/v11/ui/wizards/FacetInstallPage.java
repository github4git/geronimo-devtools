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
package org.apache.geronimo.st.v11.ui.wizards;

import org.apache.geronimo.st.v11.core.DeploymentPlanInstallConfig;
import org.apache.geronimo.st.v11.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;

public class FacetInstallPage extends AbstractFacetWizardPage {

	private DeploymentPlanInstallConfig config;

	private Text groupText;
	private Text artifactText;
	private Text versionText;
	private Text typeText;
	private Button sharedLib;

	public FacetInstallPage() {
		super("geronimo.plan.install");
		setTitle("Geronimo Deployment Plan");
		setDescription("Configure the geronimo deployment plan.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.project.facet.ui.IFacetWizardPage#setConfig(java.lang.Object)
	 */
	public void setConfig(Object config) {
		this.config = (DeploymentPlanInstallConfig) config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		Label groupLabel = new Label(composite, SWT.NONE);
		groupLabel.setText(Messages.groupId);

		groupText = new Text(composite, SWT.BORDER);
		groupText.setLayoutData(createGridData());
		groupText.setText("default");
		
		Label artifactLabel = new Label(composite, SWT.NONE);
		artifactLabel.setText(Messages.artifactId);

		artifactText = new Text(composite, SWT.BORDER);
		artifactText.setLayoutData(createGridData());
		
		Label versionLabel = new Label(composite, SWT.NONE);
		versionLabel.setText(Messages.version);

		versionText = new Text(composite, SWT.BORDER);
		versionText.setLayoutData(createGridData());
		versionText.setText("1.0");
		
		Label typeLabel = new Label(composite, SWT.NONE);
		typeLabel.setText(Messages.artifactType);

		typeText = new Text(composite, SWT.BORDER);
		typeText.setLayoutData(createGridData());
		typeText.setText("car");
		
		sharedLib = new Button(composite, SWT.CHECK);
		GridData data = createGridData();
		data.horizontalSpan = 2;
		data.verticalIndent = 5;
		sharedLib.setLayoutData(data);
		sharedLib.setText(Messages.addSharedLib);
		
		setControl(composite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage#transferStateToConfig()
	 */
	public void transferStateToConfig() {
		config.setGroupId(groupText.getText());
		config.setArtifactId(artifactText.getText());
		config.setVersion(versionText.getText());
		config.setType(typeText.getText());
		config.setSharedLib(sharedLib.getSelection());
	}

	private static GridData createGridData() {
		return new GridData(GridData.FILL_HORIZONTAL);
	}
}
