/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.arc4eclipse.panelExerciser.fixtures;

import static org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants.*;

import java.util.Map;

import org.arc4eclipse.jdtBinding.mocks.IBindingBuilder;
import org.arc4eclipse.panelExerciser.JarDataAndPath;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.jdt.core.dom.IBinding;

public class ApacheTestFixture {

	private final static String orgUrlApache = "http://www.apache.org";
	private final static String projUrlLog4j = "http://logging.apache.org/";
	public static final String log4jJar = "log4j-1.2.16.jar";
	public final static Map<String, Object> orgLog4J = Maps.<String, Object> makeMap(//
			organisationNameKey, "The Apache Software Foundation",//
			organisationUrlKey, orgUrlApache,//
			descriptionKey, "Provides support for the Apache community of open-source software projects.");
	public final static Map<String, Object> projLog4J = Maps.<String, Object> makeMap(//
			organisationUrlKey, orgUrlApache,//
			projectUrlKey, projUrlLog4j,//
			descriptionKey, "The Apache Logging Services Project creates and maintains open-source software related to the logging of application behavior and released at no charge to the public.");

	public final static JarDataAndPath jarLog4J1_2_16 = new JarDataAndPath(log4jJar, ApacheTestFixture.class,//
			organisationUrlKey, orgUrlApache,//
			projectUrlKey, projUrlLog4j,//
			descriptionKey, "",//
			javadocKey, "http://repo1.maven.org/maven2/log4j/log4j/1.2.16/log4j-1.2.16-javadoc.jar",//
			sourceKey, "http://repo1.maven.org/maven2/log4j/log4j/1.2.16/log4j-1.2.16-sources.jar");
	public final static IBinding Method_AppenderSkeleton_doAppend = IBindingBuilder.Utils.//
			parent(log4jJar, ApacheTestFixture.class).withPackage("org.apache.log4j ").withClass("AppenderSkeleton").//
			child().withMethod("doAppend");
	public final static IBinding Method_AppenderSkeleton_Append = IBindingBuilder.Utils.//
			parent(log4jJar, ApacheTestFixture.class).withPackage("org.apache.log4j ").withClass("AppenderSkeleton").//
			child().withMethod("append");
	public final static IBinding LocalVariable_AppenderSkeleton_headFilter = IBindingBuilder.Utils.//
			parent(log4jJar, ApacheTestFixture.class).withPackage("org.apache.log4j ").withClass("AppenderSkeleton").//
			child().withLocalVariable("headFilter");

}