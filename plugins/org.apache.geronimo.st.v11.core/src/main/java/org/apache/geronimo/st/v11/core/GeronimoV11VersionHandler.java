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
package org.apache.geronimo.st.v11.core;

import javax.enterprise.deploy.spi.TargetModuleID;

import org.apache.geronimo.deployment.plugin.TargetModuleIDImpl;
import org.apache.geronimo.st.v11.core.IGeronimoVersionHandler;
import org.apache.geronimo.st.v11.core.GeronimoV11Utils;
import org.eclipse.wst.server.core.IModule;

/**
 * @version $Rev: 471551 $ $Date: 2006-11-05 17:47:11 -0500 (Sun, 05 Nov 2006) $
 */
public class GeronimoV11VersionHandler implements IGeronimoVersionHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.geronimo.st.core.IGeronimoVersionHandler#getConfigID(org.eclipse.wst.server.core.IModule)
	 */
	public String getConfigID(IModule module) throws Exception {
		return GeronimoV11Utils.getConfigId(module);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.geronimo.st.core.IGeronimoVersionHandler#createTargetModuleId(java.lang.String)
	 */
	public TargetModuleID createTargetModuleId(String configId) {
		return new TargetModuleIDImpl(null, configId);
	}
}
