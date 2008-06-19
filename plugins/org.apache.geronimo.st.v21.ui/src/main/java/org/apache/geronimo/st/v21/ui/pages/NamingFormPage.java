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
package org.apache.geronimo.st.v21.ui.pages;

import java.util.List;

import org.apache.geronimo.jee.applicationclient.ApplicationClient;
import org.apache.geronimo.jee.web.WebApp;
import org.apache.geronimo.st.ui.CommonMessages;
import org.apache.geronimo.st.ui.editors.AbstractGeronimoDeploymentPlanEditor;
import org.apache.geronimo.st.ui.pages.AbstractGeronimoFormPage;
import org.apache.geronimo.st.v21.ui.sections.EjbLocalRefSection;
import org.apache.geronimo.st.v21.ui.sections.EjbRefSection;
import org.apache.geronimo.st.v21.ui.sections.GBeanRefSection;
import org.apache.geronimo.st.v21.ui.sections.ResourceEnvRefSection;
import org.apache.geronimo.st.v21.ui.sections.ResourceRefSection;
import org.apache.geronimo.st.v21.ui.sections.ServiceRefSection;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

public class NamingFormPage extends AbstractGeronimoFormPage {

    public List resRefs;

    public List resEnvRefs;

    public List ejbRefs;

    public List ejbLocalRefs;

    public List gbeanRefs;

    public List serviceRefs;

    public NamingFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        if (WebApp.class.isInstance (((AbstractGeronimoDeploymentPlanEditor) getEditor()).getDeploymentPlan().getValue())) {
            WebApp webapp = (WebApp)((AbstractGeronimoDeploymentPlanEditor) getEditor()).getDeploymentPlan().getValue();
            resRefs = webapp.getResourceRef();
            resEnvRefs = webapp.getResourceEnvRef();
            ejbRefs = webapp.getEjbRef();
            ejbLocalRefs = webapp.getEjbLocalRef();
            serviceRefs = webapp.getServiceRef();
        } else if (ApplicationClient.class.isInstance (((AbstractGeronimoDeploymentPlanEditor) getEditor()).getDeploymentPlan().getValue())) {
            ApplicationClient appClient = (ApplicationClient)((AbstractGeronimoDeploymentPlanEditor) getEditor()).getDeploymentPlan().getValue();
            resRefs = appClient.getResourceRef();
            resEnvRefs = appClient.getResourceEnvRef();
            ejbRefs = appClient.getEjbRef();
            gbeanRefs = appClient.getGbeanRef();
            serviceRefs = appClient.getServiceRef();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.geronimo.ui.pages.AbstractGeronimoFormPage#fillBody(org.eclipse.ui.forms.IManagedForm)
     */
    protected void fillBody(IManagedForm managedForm) {
        managedForm.addPart(new ResourceRefSection(getDeploymentPlan(), body, toolkit, getStyle(), resRefs));
        managedForm.addPart(new ResourceEnvRefSection(getDeploymentPlan(), body, toolkit, getStyle(), resEnvRefs));
        managedForm.addPart(new EjbRefSection(getDeploymentPlan(), body, toolkit, getStyle(), ejbRefs));
        if (WebApp.class.isInstance (getDeploymentPlan().getValue())) {
            managedForm.addPart(new EjbLocalRefSection(getDeploymentPlan(), body, toolkit, getStyle(), ejbLocalRefs));
        } else if (ApplicationClient.class.isInstance (getDeploymentPlan().getValue())){
            managedForm.addPart(new GBeanRefSection(getDeploymentPlan(), body, toolkit, getStyle(), gbeanRefs));
        }
        managedForm.addPart(new ServiceRefSection(getDeploymentPlan(), body, toolkit, getStyle(), serviceRefs));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.geronimo.ui.pages.AbstractGeronimoFormPage#getFormTitle()
     */
    public String getFormTitle() {
        return CommonMessages.namingFormPageTitle;
    }
}