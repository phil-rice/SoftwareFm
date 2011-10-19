package org.softwareFm.mavenImporter;

import java.util.List;

public interface IMavenVisitor {

	void startingTag(String baseUrl, String tagName);

	void finishedTag(String baseUrl, String tagName);

	void startingProject(String baseUrl, String tagName, String projectName, String projectUrl);

	void finishedProject(String baseUrl, String tagName, String projectName, String projectUrl);

	void version(String baseUrl, String projectName, String version, String versionUrl, String jarUrl, String pomUrl, String pom, List<String> packages);


}
