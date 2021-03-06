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
package org.apache.geronimo.eclipse.devtools;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.osgi.framework.Constants;

/**
 * @goal validatemanifest
 */
public class BundleManifestMojo extends AbstractMojo {

	/**
	 * @parameter expression="${project}"
	 * @required
	 */
	private MavenProject project;

	/**
	 * @parameter expression="${project.basedir}/META-INF/MANIFEST.MF"
	 * @required
	 */
	private File manifestFile;

	/**
	 * @parameter
	 */
	private String classpathEntriesDir;

	private Attributes attributes = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			FileInputStream fis = new FileInputStream(manifestFile);
			Manifest manifest = new Manifest(fis);
			attributes = manifest.getMainAttributes();
			validate(Constants.BUNDLE_SYMBOLICNAME, project.getName() + ";singleton:=true");
			validate(Constants.BUNDLE_VERSION, project.getVersion());
			validateBundleClasspath();
			fis.close();
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void validateBundleClasspath() throws MojoFailureException {
		if (classpathEntriesDir == null)
			return;
		File libs[] = new File(project.getBasedir(), classpathEntriesDir).listFiles();
		if (libs == null)
			return;
		String classpath = attributes.getValue(Constants.BUNDLE_CLASSPATH);
		StringTokenizer tokenizer = new StringTokenizer(classpath, ",");
		List cpEntries = new ArrayList();
		while (tokenizer.hasMoreTokens()) {
			cpEntries.add(tokenizer.nextToken().trim());
		}
		List missingEntries = new ArrayList();
		for (int i = 0; i < libs.length; i++) {
			File lib = libs[i];
			String expectedEntry = classpathEntriesDir + "/" + lib.getName();
			if (!cpEntries.contains(expectedEntry)) {
				missingEntries.add(expectedEntry);
			}
		}
		if (!missingEntries.isEmpty()) {
			throw new MojoFailureException("Manifest Bundle-Classpath is missing the following entries: " + missingEntries.toString());
		}
	}

	private void validate(String attribute, String correctValue) throws MojoFailureException {
		String currentValue = attributes.getValue(attribute).replaceAll(" ", "");
		correctValue = correctValue.replaceAll(" ", "");
		if (!correctValue.equals(currentValue)) {
			throw new MojoFailureException("Attribute value for " + attribute + " in bundle manifest is incorrect. [Found: " + currentValue + "] [Expected: " + correctValue + "]");
		}
	}

}
