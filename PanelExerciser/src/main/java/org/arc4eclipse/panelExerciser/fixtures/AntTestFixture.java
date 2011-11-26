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

public class AntTestFixture {
	// Ant
	public static final String jarAntContrib = "ant-contrib-1.0b3.jar";
	private final static String orgUrlAntContrib = "http://ant.apache.org/";
	private final static String projUrlAntContrib = "http://ant-contrib.sourceforge.net/";
	public final static Map<String, Object> orgAntContrib = Maps.<String, Object> makeMap(//
			organisationNameKey, "Apache Ant",//
			organisationUrlKey, orgUrlAntContrib,//
			descriptionKey, "");
	public final static Map<String, Object> projAntContrib = Maps.<String, Object> makeMap(//
			organisationUrlKey, orgUrlAntContrib,//
			projectUrlKey, projUrlAntContrib,//
			descriptionKey, "The Ant-Contrib project is a collection of tasks (and at one point maybe types and other tools) for Apache Ant.");

	public final static IBinding Utils$ColumnName = IBindingBuilder.Utils.//
			parent(jarAntContrib, AntTestFixture.class).withPackage("net.sf.antcontrib.logic").withClass("AntCallBack ").//
			child().withMethod("init");

	public final static JarDataAndPath jarAntContrib1_0b3 = new JarDataAndPath(jarAntContrib, AntTestFixture.class,//
			organisationUrlKey, orgUrlAntContrib,//
			projectUrlKey, projUrlAntContrib,//
			descriptionKey, "");
}