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

package org.apache.geronimo.st.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.apache.geronimo.jee.application.Application;
import org.apache.geronimo.jee.connector.Connector;
import org.apache.geronimo.jee.deployment.Artifact;
import org.apache.geronimo.jee.deployment.Dependencies;
import org.apache.geronimo.jee.deployment.Dependency;
import org.apache.geronimo.jee.deployment.Environment;
import org.apache.geronimo.jee.deployment.ObjectFactory;
import org.apache.geronimo.jee.openejb.OpenejbJar;
import org.apache.geronimo.jee.web.WebApp;
import org.apache.geronimo.st.core.GeronimoUtils;
import org.apache.geronimo.st.core.jaxb.JAXBUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * <b>DependencyHelper</b> is a helper class with various methods to aid in the discovery of
 * inter-dependencies between modules being deployed from the GEP to the Geronimo server. It
 * performs the following capabilities:
 * <ol>
 *      <li>Discovery of dependencies between modules<p>
 *      <li>Provides the proper publishing ordering of the modules based on the discovered
 *          dependencies<p>
 *      <li><b>TODO:</b> Query the server searching for missing dependencies
 * </ol>
 * 
 * @version $Rev$ $Date$
 */
public class DependencyHelper {

    private DependencyManager dm = new DependencyManager();
    private ObjectFactory deploymentFactory = new ObjectFactory();
    private List inputModules = new ArrayList();
    private List inputDeltaKind = new ArrayList();
    private List reorderedModules = new ArrayList();
    private List reorderedKinds  = new ArrayList();
    private List<JAXBElement> inputJAXBElements = new ArrayList();
    private List<JAXBElement> reorderedJAXBElements = new ArrayList();


    /**
     * Reorder the publish order of the modules based on any discovered dependencies
     * 
     * @param modules   Modules to be published to the Geronimo server
     * @param deltaKind Publish kind constant for each module
     * 
     * @return List of reordered modules and deltaKind (or input if no change)
     */
    public List reorderModules( List modules, List deltaKind ) {
        Trace.tracePoint("Entry", "DependencyHelper.reorderModules", modules, deltaKind);

        if (modules.size() == 0) {
            List reorderedLists = new ArrayList(2);
            reorderedLists.add(modules);
            reorderedLists.add(deltaKind);
            Trace.tracePoint("Exit ", "DependencyHelper.reorderModules", reorderedLists);
            return reorderedLists;
        }

        inputModules = modules;
        inputDeltaKind = deltaKind;

        // 
        // Iterate through all the modules and register the dependencies
        // 
        for (int ii=0; ii<modules.size(); ii++) {
            IModule[] module = (IModule[]) modules.get(ii);
            int moduleDeltaKind = ((Integer)deltaKind.get(ii)).intValue();
            if (moduleDeltaKind != ServerBehaviourDelegate.REMOVED) {
                Environment environment = getEnvironment(module[0]);
                if (environment != null) {
                    Artifact child = environment.getModuleId();
                    Dependencies dependencies = environment.getDependencies();
                    if (dependencies != null) {
                        List<Dependency> depList = dependencies.getDependency();
                        for ( Dependency dep : depList) {
                            Artifact parent = deploymentFactory.createArtifact();
                            parent.setGroupId( dep.getGroupId() );
                            parent.setArtifactId( dep.getArtifactId() );
                            parent.setVersion( dep.getVersion() );
                            parent.setType( dep.getType() );
                            dm.addDependency( child, parent );
                        }
                    }
                }
            }
        }

        // 
        // Iterate through all the modules again and reorder as necessary
        // 
        for (int ii=0; ii<modules.size(); ii++) {
            IModule[] module = (IModule[]) modules.get(ii);
            int moduleDeltaKind = ((Integer)deltaKind.get(ii)).intValue();
            if (module!=null && !reorderedModules.contains(module)) {
                // Not already moved 
                if (moduleDeltaKind == ServerBehaviourDelegate.REMOVED) {
                    // Move module if going to be removed 
                    reorderedModules.add(module);
                    reorderedKinds.add(moduleDeltaKind);
                }
                else {
                    Environment environment = getEnvironment(module[0]);
                    if (environment != null) {
                        Artifact artifact = environment.getModuleId();
                        if (artifact == null) {
                            // Move if null (nothing can be done)
                            if (!reorderedModules.contains(module)) {
                                reorderedModules.add(module);
                                reorderedKinds.add(moduleDeltaKind);
                            }
                        }
                        else if (dm.getParents(artifact).contains(artifact) ||  
                                 dm.getChildren(artifact).contains(artifact)) {
                            // Move if a tight circular dependency (nothing can be done)
                            if (!reorderedModules.contains(module)) {
                                reorderedModules.add(module);
                                reorderedKinds.add(moduleDeltaKind);
                            }
                        }
                        else if (dm.getParents(artifact).size() == 0) {
                            // Move if no parents (nothing to do)
                            if (!reorderedModules.contains(module)) {
                                reorderedModules.add(module);
                                reorderedKinds.add(moduleDeltaKind);
                            }
                        }
                        else if (dm.getParents(artifact).size() > 0) {
                            // Move parents first
                            processParents(dm.getParents(artifact), artifact);
                            // Move self 
                            if (!reorderedModules.contains(module)) {
                                reorderedModules.add(module);
                                reorderedKinds.add(moduleDeltaKind);
                            }
                        }
                    }
                }
            }
        }

        // 
        // Ensure return lists are exactly the same size as the input lists 
        // 
        assert reorderedModules.size() == modules.size();
        assert reorderedKinds.size() == deltaKind.size();

        // 
        // Return List of lists
        // 
        List reorderedLists = new ArrayList(2);
        reorderedLists.add(reorderedModules);
        reorderedLists.add(reorderedKinds);

        Trace.tracePoint("Exit ", "DependencyHelper.reorderModules", reorderedLists);
        return reorderedLists;
    }


    /**
     * Reorder the list of JAXBElements based on any discovered dependencies
     * 
     * @param jaxbElements
     * 
     * @return List of JAXBElements (or input if no change)
     */
    public List<JAXBElement> reorderJAXBElements( List<JAXBElement> jaxbElements ) {
        Trace.tracePoint("Entry", "DependencyHelper.reorderModules", jaxbElements);

        if (jaxbElements.size() == 0) {
            Trace.tracePoint("Exit ", "DependencyHelper.reorderModules", jaxbElements);
            return jaxbElements;
        }

        inputJAXBElements = jaxbElements;

        // 
        // Iterate through all the JAXBElements and register the dependencies
        // 
        for (JAXBElement jaxbElement : jaxbElements) {
            Environment environment = getEnvironment(jaxbElement);
            if (environment != null) {
                Artifact child = environment.getModuleId();
                if (child != null) {
                    Dependencies dependencies = environment.getDependencies();
                    if (dependencies != null) {
                        List<Dependency> depList = dependencies.getDependency();
                        if (depList != null) {
                            for ( Dependency dep : depList) {
                                Artifact parent = deploymentFactory.createArtifact();
                                parent.setGroupId( dep.getGroupId() );
                                parent.setArtifactId( dep.getArtifactId() );
                                parent.setVersion( dep.getVersion() );
                                parent.setType( dep.getType() );
                                dm.addDependency( child, parent );
                            }
                        }
                    }
                }
            }
        }

        // 
        // Iterate through all the JAXBElements again and reorder as necessary
        // 
        for (JAXBElement jaxbElement : jaxbElements) {
            if (!reorderedJAXBElements.contains(jaxbElement)) {
                // Not already moved
                Environment environment = getEnvironment(jaxbElement);
                if (environment != null) {
                    Artifact artifact = environment.getModuleId();
                    if (artifact == null) {
                        // Move if null (nothing can be done)
                        if (!reorderedJAXBElements.contains(jaxbElement)) {
                            reorderedJAXBElements.add(jaxbElement);
                        }
                    }
                    else if (dm.getParents(artifact).contains(artifact) ||  
                             dm.getChildren(artifact).contains(artifact)) {
                        // Move if a tight circular dependency (nothing can be done)
                        if (!reorderedJAXBElements.contains(jaxbElement)) {
                            reorderedJAXBElements.add(jaxbElement);
                        }
                    }
                    else if (dm.getParents(artifact).size() == 0) {
                        // Move if no parents (nothing to do)
                        if (!reorderedJAXBElements.contains(jaxbElement)) {
                            reorderedJAXBElements.add(jaxbElement);
                        }
                    }
                    else if (dm.getParents(artifact).size() > 0) {
                        // Move parents first
                        processJaxbParents(dm.getParents(artifact), artifact);
                        // Move self 
                        if (!reorderedJAXBElements.contains(jaxbElement)) {
                            reorderedJAXBElements.add(jaxbElement);
                        }
                    }
                }
            }
        }

        // 
        // Ensure return list is exactly the same size as the input list 
        // 
        assert reorderedJAXBElements.size() == jaxbElements.size();

        // 
        // Return List of JAXBElements
        // 
        Trace.tracePoint("Exit ", "DependencyHelper.reorderModules", reorderedJAXBElements);
        return reorderedJAXBElements;
    }


    /**
     *
     */
    public void close() {
        dm.close();
    }


    /*--------------------------------------------------------------------------------------------*\
    |                                                                                              |
    |  Private method(s)                                                                           | 
    |                                                                                              |
    \*--------------------------------------------------------------------------------------------*/

    /**
     * Process the parents for a given artifact. The terminatingArtifact parameter will be used as
     * the terminating condition to ensure there will not be an infinite loop (i.e., if
     * terminatingArtifact is encountered again there is a circular dependency).
     * 
     * @param parents
     * @param terminatingArtifact
     */
    private void processParents(Set parents, Artifact terminatingArtifact) {
        Trace.tracePoint("Enter", "DependencyHelper.processParents", parents, terminatingArtifact );

        if (parents == null) {
            Trace.tracePoint("Exit ", "DependencyHelper.processParents", null);
            return;
        }
        for (Iterator ii = parents.iterator(); ii.hasNext();) {
            Artifact artifact = (Artifact)ii.next();
            if (dm.getParents(artifact).size() > 0 && !artifact.equals(terminatingArtifact)) {
                // Keep processing parents (as long as no circular dependencies)
                processParents(dm.getParents(artifact), terminatingArtifact);
                // Move self 
                IModule[] module = getModule(artifact);
                int moduleDeltaKind = getDeltaKind(artifact);
                if (module!=null && !reorderedModules.contains(module)) {
                    reorderedModules.add(module);
                    reorderedKinds.add(moduleDeltaKind);
                }
            }
            else {
                // Move parent
                IModule[] module = getModule(artifact);
                int moduleDeltaKind = getDeltaKind(artifact);
                if (module!=null && !reorderedModules.contains(module)) {
                    reorderedModules.add(module);
                    reorderedKinds.add(moduleDeltaKind);
                }
            }
        }

        Trace.tracePoint("Exit ", "DependencyHelper.processParents");
    }


    /**
     * Returns the Environment for the given IModule
     * 
     * @param module IModule to be published
     * 
     * @return Environment
     */
    private Environment getEnvironment(IModule module) {
        Trace.tracePoint("Enter", "DependencyHelper.getEnvironment", module);

        Environment environment = null;
        if (GeronimoUtils.isWebModule(module)) {
            WebApp plan = getWebDeploymentPlan(module).getValue();
            if (plan != null)
                environment = plan.getEnvironment();
        }
        else if (GeronimoUtils.isEjbJarModule(module)) {
            OpenejbJar plan = getOpenEjbDeploymentPlan(module).getValue();
            if (plan != null)
                environment = plan.getEnvironment();
        }
        else if (GeronimoUtils.isEarModule(module)) {
            Application plan = getApplicationDeploymentPlan(module).getValue();
            if (plan != null)
                environment = plan.getEnvironment();
        }
        else if (GeronimoUtils.isRARModule(module)) {
            Connector plan = getConnectorDeploymentPlan(module).getValue();
            if (plan != null)
                environment = plan.getEnvironment();
        }

        Trace.tracePoint("Exit ", "DependencyHelper.getEnvironment", environment);
        return environment;
    }


    /**
     * Return the IModule[] for a given artifact
     * 
     * @param artifact
     * 
     * @return IModule[]
     */
    private IModule[] getModule(Artifact artifact) {
        Trace.tracePoint("Enter", "DependencyHelper.getModule", artifact);

        for (int ii=0; ii<inputModules.size(); ii++) {
            IModule[] module = (IModule[]) inputModules.get(ii);
            int moduleDeltaKind = ((Integer)inputDeltaKind.get(ii)).intValue();
            Environment environment = getEnvironment(module[0]);
            if (environment != null) {
                Artifact moduleArtifact = environment.getModuleId();
                if (artifact.equals(moduleArtifact)) {
                    Trace.tracePoint("Exit ", "DependencyHelper.getModule", module);
                    return module;
                }
            }
        }

        Trace.tracePoint("Exit ", "DependencyHelper.getModule", null);
        return null;
    }


    /**
     * Return the deltaKind array index for a given artifact
     * 
     * @param artifact
     * 
     * @return int
     */
    private int getDeltaKind(Artifact artifact) {
        Trace.tracePoint("Enter", "DependencyHelper.getDeltaKind", artifact);

        for (int ii=0; ii<inputModules.size(); ii++) {
            IModule[] module = (IModule[]) inputModules.get(ii);
            int moduleDeltaKind = ((Integer)inputDeltaKind.get(ii)).intValue();
            Environment environment = getEnvironment(module[0]);
            if (environment != null) {
                Artifact moduleArtifact = environment.getModuleId();
                if (artifact.equals(moduleArtifact)) {
                    Trace.tracePoint("Exit ", "DependencyHelper.getDeltaKind", moduleDeltaKind);
                    return moduleDeltaKind;
                }
            }
        }
        Trace.tracePoint("Exit ", "DependencyHelper.getDeltaKind", 0);
        return 0;
    }


    /**
     * Returns the WebApp for the given IModule
     * 
     * @param module IModule to be published
     * 
     * @return WebApp
     */
    private JAXBElement<WebApp> getWebDeploymentPlan(IModule module) {
        Trace.tracePoint("Enter", "DependencyHelper.getWebDeploymentPlan", module);

        IVirtualComponent comp = GeronimoUtils.getVirtualComponent(module);
        IFile file = GeronimoUtils.getWebDeploymentPlanFile(comp);
        if (file.getName().equals(GeronimoUtils.WEB_PLAN_NAME) && file.exists()) {
            Trace.tracePoint("Exit ", "DependencyHelper.getWebDeploymentPlan", JAXBUtils.unmarshalDeploymentPlan(file));
            return JAXBUtils.unmarshalDeploymentPlan(file);
        }

        Trace.tracePoint("Exit ", "DependencyHelper.getWebDeploymentPlan", null);
        return null;
    }


    /**
     * Returns the OpenEjbJar for the given IModule
     * 
     * @param module IModule to be published
     * 
     * @return OpenEjbJar
     */
    private JAXBElement<OpenejbJar> getOpenEjbDeploymentPlan(IModule module) {
        Trace.tracePoint("Enter", "DependencyHelper.getOpenEjbDeploymentPlan", module);

        IVirtualComponent comp = GeronimoUtils.getVirtualComponent(module);
        IFile file = GeronimoUtils.getOpenEjbDeploymentPlanFile(comp);
        if (file.getName().equals(GeronimoUtils.OPENEJB_PLAN_NAME) && file.exists()) {
            Trace.tracePoint("Exit ", "DependencyHelper.getOpenEjbDeploymentPlan", JAXBUtils.unmarshalDeploymentPlan(file));
            return JAXBUtils.unmarshalDeploymentPlan(file);
        }

        Trace.tracePoint("Exit ", "DependencyHelper.getOpenEjbDeploymentPlan", null);
        return null;
    }


    /**
     * Returns the Application for the given IModule
     * 
     * @param module IModule to be published
     * 
     * @return Application
     */
    private JAXBElement<Application> getApplicationDeploymentPlan(IModule module) {
        Trace.tracePoint("Enter", "DependencyHelper.getApplicationDeploymentPlan", module);

        IVirtualComponent comp = GeronimoUtils.getVirtualComponent(module);
        IFile file = GeronimoUtils.getApplicationDeploymentPlanFile(comp);
        if (file.getName().equals(GeronimoUtils.APP_PLAN_NAME) && file.exists()) {
            Trace.tracePoint("Exit ", "DependencyHelper.getApplicationDeploymentPlan", JAXBUtils.unmarshalDeploymentPlan(file));
            return JAXBUtils.unmarshalDeploymentPlan(file);
        }

        Trace.tracePoint("Exit ", "DependencyHelper.getApplicationDeploymentPlan", null);
        return null;
    }


    /**
     * Returns the Connector for the given IModule
     * 
     * @param module IModule to be published
     * 
     * @return Application
     */
    private JAXBElement<Connector> getConnectorDeploymentPlan(IModule module) {
        Trace.tracePoint("Enter", "DependencyHelper.getConnectorDeploymentPlan", module);

        IVirtualComponent comp = GeronimoUtils.getVirtualComponent(module);
        IFile file = GeronimoUtils.getConnectorDeploymentPlanFile(comp);
        if (file.getName().equals(GeronimoUtils.CONNECTOR_PLAN_NAME) && file.exists()) {
            Trace.tracePoint("Exit ", "DependencyHelper.getConnectorDeploymentPlan", JAXBUtils.unmarshalDeploymentPlan(file));
            return JAXBUtils.unmarshalDeploymentPlan(file);
        }

        Trace.tracePoint("Exit ", "DependencyHelper.getConnectorDeploymentPlan", null);
        return null;
    }


    /**
     * Process the parents for a given artifact. The terminatingArtifact parameter will be used as
     * the terminating condition to ensure there will not be an infinite loop (i.e., if
     * terminatingArtifact is encountered again there is a circular dependency).
     * 
     * @param parents
     * @param terminatingArtifact
     */
    private void processJaxbParents(Set parents, Artifact terminatingArtifact) {
        Trace.tracePoint("Enter", "DependencyHelper.processJaxbParents", parents, terminatingArtifact );

        if (parents == null) {
            Trace.tracePoint("Exit ", "DependencyHelper.processJaxbParents", null);
            return;
        }
        for (Iterator ii = parents.iterator(); ii.hasNext();) {
            Artifact artifact = (Artifact)ii.next();
            if (dm.getParents(artifact).size() > 0 && !artifact.equals(terminatingArtifact)) {
                // Keep processing parents (as long as no circular dependencies)
                processJaxbParents(dm.getParents(artifact), terminatingArtifact);
                // Move self 
                JAXBElement jaxbElement = getJaxbElement(artifact);
                if (jaxbElement != null) {
                    if (!reorderedJAXBElements.contains(jaxbElement)) {
                        reorderedJAXBElements.add(jaxbElement);
                    }
                }
            }
            else {
                // Move parent
                JAXBElement jaxbElement = getJaxbElement(artifact);
                if (jaxbElement != null) {
                    if (!reorderedJAXBElements.contains(jaxbElement)) {
                        reorderedJAXBElements.add(jaxbElement);
                    }
                }
            }
        }

        Trace.tracePoint("Exit ", "DependencyHelper.processJaxbParents");
    }


    /**
     * Returns the Environment for the given JAXBElement plan
     * 
     * @param jaxbElement JAXBElement plan
     * 
     * @return Environment
     */
    private Environment getEnvironment(JAXBElement jaxbElement) {
        Trace.tracePoint("Enter", "DependencyHelper.getEnvironment", jaxbElement);

        Environment environment = null;
        Object plan = jaxbElement.getValue();
        if (WebApp.class.isInstance(plan)) {
            if (plan != null)
                environment = ((WebApp)plan).getEnvironment();
        }
        else if (OpenejbJar.class.isInstance(plan)) {
            if (plan != null)
                environment = ((OpenejbJar)plan).getEnvironment();
        }
        else if (Application.class.isInstance(plan)) {
            if (plan != null)
                environment = ((Application)plan).getEnvironment();
        }
        else if (Connector.class.isInstance(plan)) {
            if (plan != null)
                environment = ((Connector)plan).getEnvironment();
        }

        Trace.tracePoint("Exit ", "DependencyHelper.getEnvironment", environment);
        return environment;
    }


    /**
     * Return the JAXBElement for a given artifact
     * 
     * @param artifact
     * 
     * @return JAXBElement
     */
    private JAXBElement getJaxbElement(Artifact artifact) {
        Trace.tracePoint("Enter", "DependencyHelper.getJaxbElement", artifact);

        for (JAXBElement jaxbElement : inputJAXBElements) {
            Environment environment = getEnvironment(jaxbElement);
            if (environment != null) {
                Artifact jaxbArtifact = environment.getModuleId();
                if (artifact.equals(jaxbArtifact)) {
                    Trace.tracePoint("Exit ", "DependencyHelper.getJaxbElement", jaxbElement);
                    return jaxbElement;
                }
            }
        }

        // TODO: Query the server searching for missing dependencies
        Trace.tracePoint("Exit ", "DependencyHelper.getJaxbElement", null);
        return null;
    }
}