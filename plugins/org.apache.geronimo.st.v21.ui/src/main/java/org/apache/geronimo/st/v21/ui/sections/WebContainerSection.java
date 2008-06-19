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

import javax.xml.bind.JAXBElement;

import org.apache.geronimo.jee.naming.GbeanLocator;
import org.apache.geronimo.jee.naming.ObjectFactory;
import org.apache.geronimo.jee.naming.Pattern;
import org.apache.geronimo.jee.web.WebApp;
import org.apache.geronimo.st.ui.sections.AbstractSectionPart;
import org.apache.geronimo.st.v21.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class WebContainerSection extends AbstractSectionPart {

    protected Text gBeanLink;

    protected Text artifact;

    protected Text group;

    protected Text module;

    protected Text name;

    protected Text version;

    protected Button specifyAsLink;

    protected Button specifyAsPattern;

    WebApp plan;
    
    private ObjectFactory namingFactory;

    /**
     * @param parent
     * @param toolkit
     * @param style
     * @param plan
     */
    public WebContainerSection(Composite parent, FormToolkit toolkit, int style, JAXBElement plan) {
        super(parent, toolkit, style, plan);
        this.plan = (WebApp) plan.getValue();
        namingFactory = new ObjectFactory();
        createClient();
    }

    protected void createClient() {
        Section section = getSection();

        section.setText(Messages.webContainerSection);
        section.setDescription(Messages.webContainerSectionDescription);
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

        specifyAsLink = toolkit.createButton(composite, Messages.useGBeanLink, SWT.RADIO);
        GridData data = new GridData();
        data.horizontalSpan = 2;
        specifyAsLink.setLayoutData(data);

        GbeanLocator wc = plan.getWebContainer();

        toolkit.createLabel(composite, Messages.gBeanLink);
        String value = wc != null ? wc.getGbeanLink() : null;
        gBeanLink = toolkit.createText(composite, value, SWT.BORDER);
        gBeanLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        gBeanLink.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getGBeanLocator().setGbeanLink(gBeanLink.getText());
                markDirty();
            }
        });

        specifyAsPattern = toolkit.createButton(composite, Messages.useGBeanPattern, SWT.RADIO);
        specifyAsPattern.setLayoutData(data);

        toolkit.createLabel(composite, Messages.groupId);
        value = wc != null && wc.getPattern() != null ? wc.getPattern().getGroupId()
                : null;
        group = toolkit.createText(composite, value, SWT.BORDER);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        group.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getPattern().setGroupId(group.getText());
                markDirty();
            }
        });

        toolkit.createLabel(composite, Messages.artifactId);
        value = wc != null && wc.getPattern() != null ? wc.getPattern().getArtifactId()
                : null;
        artifact = toolkit.createText(composite, value, SWT.BORDER);
        artifact.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        artifact.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getPattern().setArtifactId(artifact.getText());
                markDirty();
            }
        });

        toolkit.createLabel(composite, Messages.moduleId);
        value = wc != null && wc.getPattern() != null ? wc.getPattern().getModule()
                : null;
        module = toolkit.createText(composite, value, SWT.BORDER);
        module.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        module.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getPattern().setModule(module.getText());
                markDirty();
            }
        });

        toolkit.createLabel(composite, Messages.name);
        value = wc != null && wc.getPattern() != null ? wc.getPattern().getName()
                : null;
        name = toolkit.createText(composite, value, SWT.BORDER);
        name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        name.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getPattern().setName(name.getText());
                markDirty();
            }
        });

        toolkit.createLabel(composite, Messages.version);
        value = wc != null && wc.getPattern() != null ? wc.getPattern().getVersion()
                : null;
        version = toolkit.createText(composite, value, SWT.BORDER);
        version.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        version.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                getPattern().setVersion(version.getText());
                markDirty();
            }
        });

        specifyAsLink.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (specifyAsLink.getSelection()) {
                    getGBeanLocator().setPattern(null);
                    if (gBeanLink.getText().length() > 0) {
                        plan.getWebContainer().setGbeanLink(gBeanLink.getText());
                    }
                    markDirty();
                    toggle();
                }
            }
        });
 
        specifyAsPattern.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (specifyAsPattern.getSelection()) {
                	plan.getWebContainer().setGbeanLink (null);
                    if (group.getText().length() > 0) {
                        getPattern().setGroupId(group.getText());
                    }
                    if (artifact.getText().length() > 0) {
                        getPattern().setArtifactId(artifact.getText());
                    }
                    if (module.getText().length() > 0) {
                        getPattern().setModule(module.getText());
                    }
                    if (name.getText().length() > 0) {
                        getPattern().setName(name.getText());
                    }
                    if (version.getText().length() > 0) {
                        getPattern().setVersion(version.getText());
                    }
                    markDirty();
                    toggle();
                }
            }
        });

        if (wc != null) {
            if (wc.getGbeanLink() != null) {
                specifyAsLink.setSelection(true);
            } else if (wc.getPattern() != null) {
                specifyAsPattern.setSelection(true);
            }
        }

        toggle();
    }

    public void toggle() {
        gBeanLink.setEnabled(specifyAsLink.getSelection());
        artifact.setEnabled(specifyAsPattern.getSelection());
        group.setEnabled(specifyAsPattern.getSelection());
        module.setEnabled(specifyAsPattern.getSelection());
        name.setEnabled(specifyAsPattern.getSelection());
        version.setEnabled(specifyAsPattern.getSelection());
    }

    /**
     * @return
     */
    private GbeanLocator getGBeanLocator() {
        GbeanLocator wc = plan.getWebContainer();
        if (wc == null) {
            wc = namingFactory.createGbeanLocator();
            plan.setWebContainer(wc);
        }
        return wc;
    }

    /**
     * @return
     */
    private Pattern getPattern() {
        GbeanLocator locator = getGBeanLocator();
        Pattern pattern = locator.getPattern();
        if (pattern == null) {
            pattern = namingFactory.createPattern();
            locator.setPattern(pattern);
        }
        return pattern;
    }
}