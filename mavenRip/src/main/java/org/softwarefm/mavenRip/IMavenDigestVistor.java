package org.softwarefm.mavenRip;

import java.io.File;

import org.apache.maven.model.Model;


public interface IMavenDigestVistor {
	void visit(String pomUrl,  Model model, String groupId, String artifactId, String version, File file, String hexDigest);
}
