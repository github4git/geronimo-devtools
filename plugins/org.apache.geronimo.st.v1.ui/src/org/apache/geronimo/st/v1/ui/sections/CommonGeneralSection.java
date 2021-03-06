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
package org.apache.geronimo.st.v1.ui.sections;

import org.apache.geronimo.st.ui.CommonMessages;
import org.apache.geronimo.st.ui.sections.AbstractSectionPart;
import org.apache.geronimo.st.v1.ui.internal.Messages;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @version $Rev$ $Date$
 */
public abstract class CommonGeneralSection extends AbstractSectionPart {

	protected Text configId;

	protected Text parentId;

	public CommonGeneralSection(Composite parent, FormToolkit toolkit, int style, EObject plan) {
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

		// ------- Label and text field for the config Id -------
		createLabel(composite, Messages.editorConfigId, toolkit);

		configId = toolkit.createText(composite, getConfigID(), SWT.BORDER);
		configId.setLayoutData(createTextFieldGridData());
		configId.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setConfigID(configId.getText());
				markDirty();
			}
		});

		// ------- Label and text field for the parent Id -------
		createLabel(composite, Messages.editorParentId, toolkit);

		parentId = toolkit.createText(composite, getParentID(), SWT.BORDER);
		parentId.setLayoutData(createTextFieldGridData());
		parentId.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setParentID(parentId.getText());
				markDirty();
			}
		});
	}

	protected Label createLabel(Composite parent, String text, FormToolkit toolkit) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		return label;
	}

	protected GridData createTextFieldGridData() {
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.widthHint = 150;
		return data;
	}

	protected String getConfigID() {
		return getID(getConfigIdEAttribute());
	}

	protected String getParentID() {
		return getID(getParentIdEAttribute());
	}

	protected void setConfigID(String id) {
		getPlan().eSet(getConfigIdEAttribute(), id);
	}

	protected void setParentID(String id) {
		getPlan().eSet(getParentIdEAttribute(), id);
	}

	private String getID(EAttribute configIdAttribute) {
		if (configIdAttribute != null && getPlan().eIsSet(configIdAttribute)) {
			return getPlan().eGet(configIdAttribute).toString();
		}
		return "";
	}

	protected abstract EAttribute getConfigIdEAttribute();

	protected abstract EAttribute getParentIdEAttribute();

}
