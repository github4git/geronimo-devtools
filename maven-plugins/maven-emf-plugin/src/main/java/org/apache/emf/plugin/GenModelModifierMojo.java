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
package org.apache.emf.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Utility mojo that sets attributes in the generated .genmodel that are not
 * configurable via headless eclipse
 * 
 * @goal setattributes
 *
 * @version $Rev$ $Date$
 */
public class GenModelModifierMojo extends AbstractMojo {

	/**
	 * @parameter
	 * @required
	 */
	private File genmodel;

	/**
	 * @parameter
	 * @required
	 */
	private Map attributes;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {

		if (!genmodel.exists())
			throw new MojoFailureException(genmodel.getAbsolutePath() + " "
					+ "does not exist.");

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
			Transformer transformer = transformerFactory.newTransformer();
			Document doc = builder.parse(genmodel);

			NodeList nodeList = doc.getElementsByTagName("genmodel:GenModel");
			Element element = (Element) nodeList.item(0);
			if (element != null) {
				Iterator j = attributes.keySet().iterator();
				while (j.hasNext()) {
					String attribute = (String) j.next();
					String value = (String) attributes.get(attribute);
					element.setAttribute(attribute, value);
					getLog().debug("Attribute " + attribute + " : " + value);
				}
				Source src = new DOMSource(doc);
				Result result = new StreamResult(new FileOutputStream(genmodel));
				transformer.transform(src, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoFailureException(e.getMessage());
		}

	}
}
