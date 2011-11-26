/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.mavenImporter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class Hello {

	public static void main(String[] args) throws IOException, XmlPullParserException {
		Model model = new MavenXpp3Reader().read(new FileReader(new File("pom.xml")));
		System.out.println("modules: " + model.getModules());
		System.out.println("artefactId: " + model.getArtifactId());
		System.out.println("name: " + model.getName());
		System.out.println("url: " + model.getUrl());

	}
}