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
