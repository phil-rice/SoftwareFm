package org.softwareFm.mavenImporter;

import java.util.List;

import org.softwareFm.utilities.indent.Indent;

public interface IMavenNavigator extends IMavenVisitor {

	boolean shouldExploreTag(String tagName);

	boolean shouldExploreProject(String projectName);

	void errorProject(String baseUrl, String tagName, String projectName, String projectUrl, Throwable throwable);

	public static class Utils {
		public static IMavenNavigator sysoutNavigator() {
			return new IMavenNavigator() {
				private Indent indent = new Indent(' ', 0, 3);

				public void startingTag(String baseUrl, String tagName) {
					System.out.println(indent + "Tag: " + tagName);
					indent = indent.indent();
				}

				public void finishedTag(String baseUrl, String tagName) {
					indent = indent.unindent();
				}

				public void startingProject(String baseUrl, String tagName, String projectName, String projectUrl) {
					System.out.println(indent + "Project: " + tagName + "/" + projectName + ": " + projectUrl);
					indent = indent.indent();
				}

				public void errorProject(String baseUrl, String tagName, String projectName, String projectUrl, Throwable throwable) {
					System.err.println("Error loading project url: " + throwable.getMessage());
					indent = indent.unindent();
				}
				public void finishedProject(String baseUrl, String tagName, String projectName, String projectUrl) {
					indent = indent.unindent();
				}

				public void version(String baseUrl, String projectName, String version, String versionUrl, String jarUrl, String pomUrl, String pom, List<String> packages) {
					System.out.println(indent + "Version: " + version + ", PomUrl: " + pomUrl + ", Packages: " + packages);
				}

				public boolean shouldExploreTag(String tagName) {
					return true;
				}

				public boolean shouldExploreProject(String projectName) {
					return true;
				}

			};
		}
	}

}
