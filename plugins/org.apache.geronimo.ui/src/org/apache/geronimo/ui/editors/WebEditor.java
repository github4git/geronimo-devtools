/**
 * Copyright 2004, 2005 The Apache Software Foundation or its licensors, as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.geronimo.ui.editors;

import org.apache.geronimo.core.internal.GeronimoUtils;
import org.apache.geronimo.ui.internal.Messages;
import org.apache.geronimo.ui.pages.NamingFormPage;
import org.apache.geronimo.ui.pages.SecurityPage;
import org.apache.geronimo.ui.pages.WebGeneralPage;
import org.apache.geronimo.xml.ns.web.WebFactory;
import org.apache.geronimo.xml.ns.web.impl.WebPackageImpl;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormPage;

public class WebEditor extends AbstractGeronimoDeploymentPlanEditor {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.geronimo.ui.editors.AbstractGeronimoDeploymentPlanEditor#doAddPages()
     */
    public void doAddPages() throws PartInitException {
        addPage(new WebGeneralPage(this, "generalpage",
                Messages.editorTabGeneral));
        addPage(getNamingFormPage());
        addPage(new SecurityPage(this, "securitypage",
                Messages.editorTabSecurity, WebPackageImpl.eINSTANCE
                        .getWebAppType_Security()));
        addSourcePage();
    }

    public EObject loadDeploymentPlan(IFile file) {
        return GeronimoUtils.getWebDeploymentPlan(file);
    }

    private FormPage getNamingFormPage() {
        NamingFormPage formPage = new NamingFormPage(this, "namingpage",
                Messages.editorTabNaming);
        formPage.ejbLocalRef = WebFactory.eINSTANCE.getWebPackage()
                .getWebAppType_EjbLocalRef();
        formPage.ejbRef = WebFactory.eINSTANCE.getWebPackage()
                .getWebAppType_EjbRef();
        formPage.resEnvRef = WebFactory.eINSTANCE.getWebPackage()
                .getWebAppType_ResourceEnvRef();
        formPage.resRef = WebFactory.eINSTANCE.getWebPackage()
                .getWebAppType_ResourceRef();
        return formPage;
    }

}