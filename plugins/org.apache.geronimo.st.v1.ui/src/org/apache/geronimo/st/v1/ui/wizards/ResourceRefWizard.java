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
package org.apache.geronimo.st.v1.ui.wizards;

import org.apache.geronimo.st.ui.CommonMessages;
import org.apache.geronimo.st.ui.sections.AbstractTableSection;
import org.apache.geronimo.st.ui.wizards.AbstractTableWizard;
import org.apache.geronimo.xml.ns.naming.NamingFactory;
import org.apache.geronimo.xml.ns.naming.NamingPackage;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EFactory;

/**
 * @version $Rev$ $Date$
 */
public class ResourceRefWizard extends AbstractTableWizard {

	/**
	 * @param section
	 */
	public ResourceRefWizard(AbstractTableSection section) {
		super(section);
	}

	public EFactory getEFactory() {
		return NamingFactory.eINSTANCE;
	}

	public EAttribute[] getTableColumnEAttributes() {
		return new EAttribute[] {
				NamingPackage.eINSTANCE.getResourceRefType_RefName(),
				NamingPackage.eINSTANCE.getResourceRefType_ResourceLink(),
				NamingPackage.eINSTANCE.getResourceRefType_TargetName() };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.ui.wizards.DynamicAddEditWizard#getAddWizardWindowTitle()
	 */
	public String getAddWizardWindowTitle() {
		return CommonMessages.wizardPageTitle_ResRef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.ui.wizards.DynamicAddEditWizard#getEditWizardWindowTitle()
	 */
	public String getEditWizardWindowTitle() {
		return CommonMessages.wizardEditTitle_ResRef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.ui.wizards.DynamicAddEditWizard#getWizardFirstPageTitle()
	 */
	public String getWizardFirstPageTitle() {
		return CommonMessages.wizardPageTitle_ResRef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.ui.wizards.DynamicAddEditWizard#getWizardFirstPageDescription()
	 */
	public String getWizardFirstPageDescription() {
		return CommonMessages.wizardPageDescription_ResRef;
	}

}
