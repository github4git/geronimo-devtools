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
package org.apache.geronimo.st.v11.ui.sections;

import org.apache.geronimo.st.ui.CommonMessages;
import org.apache.geronimo.st.ui.sections.AbstractSectionPart;
import org.apache.geronimo.xml.ns.security.SecurityFactory;
import org.apache.geronimo.xml.ns.security.SecurityPackage;
import org.apache.geronimo.xml.ns.security.SecurityType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
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
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class SecurityRootSection extends AbstractSectionPart {

	EReference secERef;

	Text defaultRole;

	Button doas;

	Button useCtxHdl;

	/**
	 * @param section
	 */
	public SecurityRootSection(Section section) {
		super(section);
	}

	/**
	 * @param parent
	 * @param toolkit
	 * @param style
	 */
	public SecurityRootSection(Composite parent, FormToolkit toolkit, int style, EObject plan, EReference secERef) {
		super(parent, toolkit, style, plan);
		this.secERef = secERef;
		createClient();
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
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		section.setClient(composite);

		createLabel(composite, CommonMessages.defaultRole, toolkit);
		defaultRole = toolkit.createText(composite, getDefaultRole(), SWT.BORDER);
		GridData gd = new GridData();
		gd.widthHint = 150;
		defaultRole.setLayoutData(gd);
		defaultRole.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getSecurityType().setDefaultRole(defaultRole.getText());
				markDirty();
			}
		});

		doas = toolkit.createButton(composite, CommonMessages.doasCurrentCaller, SWT.CHECK);
		doas.setLayoutData(createGridData());
		doas.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				getSecurityType().setDoasCurrentCaller(doas.getSelection());
				markDirty();
			}
		});

		useCtxHdl = toolkit.createButton(composite, CommonMessages.useContextHandler, SWT.CHECK);
		useCtxHdl.setLayoutData(createGridData());
		useCtxHdl.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				getSecurityType().setUseContextHandler(useCtxHdl.getSelection());
				markDirty();
			}
		});

	}

	protected GridData createGridData() {
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		return gd;
	}

	protected Label createLabel(Composite parent, String text, FormToolkit toolkit) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		return label;
	}

	private String getDefaultRole() {
		SecurityType secType = (SecurityType) getPlan().eGet(secERef);
		if (secType != null
				&& secType.eIsSet(SecurityPackage.eINSTANCE.getSecurityType_DefaultRole())) {
			return secType.getDefaultRole();
		}
		return "";
	}

	private SecurityType getSecurityType() {
		SecurityType secType = (SecurityType) getPlan().eGet(secERef);
		if (secType == null) {
			secType = SecurityFactory.eINSTANCE.createSecurityType();
			getPlan().eSet(secERef, secType);
		}
		return secType;
	}

}
