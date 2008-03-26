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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.geronimo.jee.web.WebApp;
import org.apache.geronimo.st.ui.CommonMessages;
import org.apache.geronimo.st.ui.providers.AdapterFactory;
import org.apache.geronimo.st.ui.sections.AbstractTableSection;
import org.apache.geronimo.st.v21.ui.Activator;
import org.apache.geronimo.st.v21.ui.wizards.ResourceRefWizard;
import org.apache.geronimo.jee.naming.ResourceRef;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ResourceRefSection extends AbstractTableSection {

	List resourceRefERefs;

	private static final String[] COLUMN_NAMES = new String[] {
			CommonMessages.editorResRefNameTitle,
			CommonMessages.editorResRefLinkTitle};

	public ResourceRefSection(JAXBElement plan, Composite parent, FormToolkit toolkit, int style, List resourceRefERefs) {
		super(plan, parent, toolkit, style);
		this.resourceRefERefs = resourceRefERefs;
		createClient();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.ui.sections.AbstractTableSection#getTitle()
	 */
	public String getTitle() {
		return CommonMessages.editorResourceRefTitle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.ui.sections.AbstractTableSection#getDescription()
	 */
	public String getDescription() {
		return CommonMessages.editorResourceRefDescription;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.ui.sections.AbstractTableSection#getTableColumnNames()
	 */
	public String[] getTableColumnNames() {
		return COLUMN_NAMES;
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.apache.geronimo.ui.sections.AbstractTableSection#getEReference()
//	 */
//	public JAXBElement getEReference() {
//		return resourceRefERef;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.ui.sections.DynamicTableSection#getWizard()
	 */
	public Wizard getWizard() {
		return new ResourceRefWizard(this);
	}

	public ImageDescriptor getImageDescriptor() {
		return Activator.imageDescriptorFromPlugin("org.eclipse.jst.j2ee", "icons/full/obj16/resourceRef_obj.gif");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.ui.sections.AbstractTableSection#getTableEntryObjectType()
	 */
	public Class getTableEntryObjectType() {
		return ResourceRef.class;
	}
	
	public List getObjectContainer() {
		return resourceRefERefs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.st.ui.sections.AbstractTableSection#getAdapterFactory()
	 */
	public AdapterFactory getAdapterFactory() {
		return new AdapterFactory() {
			public Object[] getElements(Object inputElement) {
				if (!JAXBElement.class.isInstance(inputElement)) {
					return new String[] { "" };
				}
				JAXBElement plan = (JAXBElement)inputElement;
				if (plan.getDeclaredType().equals(WebApp.class)) {
					return ((WebApp)plan.getValue()).getResourceRef().toArray();
				}
				return new String[] { "" };
			}
			public String getColumnText(Object element, int columnIndex) {
				if (ResourceRef.class.isInstance(element)) {
					ResourceRef resourceRef = (ResourceRef)element;
					switch (columnIndex) {
					case 0: return resourceRef.getRefName();
					case 1: return resourceRef.getResourceLink();
					}
				}
				return null;
			}
		};
	}
}
