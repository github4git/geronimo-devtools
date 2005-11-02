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
package org.apache.geronimo.ui.pages;

import org.apache.geronimo.ui.sections.EjbLocalRefSection;
import org.apache.geronimo.ui.sections.EjbRefSection;
import org.apache.geronimo.ui.sections.GBeanRefSection;
import org.apache.geronimo.ui.sections.ResourceEnvRefSection;
import org.apache.geronimo.ui.sections.ResourceRefSection;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

public class NamingFormPage extends AbstractGeronimoFormPage {

    public EReference resRef;

    public EReference resEnvRef;

    public EReference ejbRef;

    public EReference ejbLocalRef;
    
    public EReference gbeanRef;

    public NamingFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public NamingFormPage(String id, String title) {
        super(id, title);
    }


    /* (non-Javadoc)
     * @see org.apache.geronimo.ui.pages.AbstractGeronimoFormPage#fillBody(org.eclipse.ui.forms.IManagedForm)
     */
    protected void fillBody(IManagedForm managedForm) {        
        managedForm.addPart(new ResourceRefSection(getDeploymentPlan(), body, toolkit, getStyle(), resRef));
        managedForm.addPart(new ResourceEnvRefSection(getDeploymentPlan(), body, toolkit, getStyle(), resEnvRef));
        managedForm.addPart(new EjbRefSection(getDeploymentPlan(), body, toolkit, getStyle(), ejbRef));
        managedForm.addPart(new EjbLocalRefSection(getDeploymentPlan(), body, toolkit, getStyle(), ejbLocalRef));
        managedForm.addPart(new GBeanRefSection(getDeploymentPlan(), body, toolkit, getStyle(), gbeanRef));
    }

}
