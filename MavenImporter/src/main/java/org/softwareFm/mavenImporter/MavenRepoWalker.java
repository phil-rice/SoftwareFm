package org.softwareFm.mavenImporter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;

public class MavenRepoWalker {

	private final int maxProjects;
	int count = 0;

	public MavenRepoWalker(int maxProjects) {
		this.maxProjects = maxProjects;
	}

	public void walkAllTags(String baseUrl, IMavenNavigator visitor) {
		try {
			String text = Files.getText(new URL(baseUrl + "/tags"));
			String divMainContent = findMainContentDiv(text);
			String[] lines = divMainContent.split("\n");
			for (String line : lines) {
				String trimmed = line.trim();
				if (trimmed.startsWith("<a href=")) {
					int index = trimmed.indexOf('"');
					int endIndex = trimmed.indexOf("\"", index + 1);
					String tagName = trimmed.substring(index + 1, endIndex);
					if (visitor.shouldExploreTag(tagName)) {
						walkTag(baseUrl, tagName, visitor);
					}
					if (shouldExit())
						return;
				}
			}
			//
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	private void walkTag(String baseUrl, String tagName, IMavenNavigator visitor) {
		if (visitor.shouldExploreTag(tagName))
			try {
				if (shouldExit())
					return;
				visitor.startingTag(baseUrl, tagName);
				String text = Files.getText(new URL(baseUrl + tagName));
				String mainContentDiv = findMainContentDiv(text);
				for (String projectLinkAndName : snip(mainContentDiv, "<a class=\"result-title\"href=\"", "</a>")) {
					int middleIndex = projectLinkAndName.indexOf("\">");
					String projectUrl = projectLinkAndName.substring(0, middleIndex);
					String projectName = projectLinkAndName.substring(middleIndex + 2);
					walkProject(baseUrl, tagName, projectName, projectUrl, visitor);
					if (shouldExit())
						return;
				}
				visitor.finishedTag(baseUrl, tagName); // definately don't want this in finally block...
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
	}

	private void walkProject(String baseUrl, String tagName, String projectName, String projectUrl, IMavenNavigator visitor) {
		if (visitor.shouldExploreProject(projectName))
			try {
				count++;
				if (shouldExit())
					return;
				visitor.startingProject(baseUrl, tagName, projectName, projectUrl);
				String text;
				try {
					text = Files.getText(new URL(baseUrl + projectUrl));
				} catch (Exception e) {
					visitor.errorProject(baseUrl, tagName, projectName, projectUrl, e);
					return;
				}
				String mainContentDiv = onlySnip(text, "<div id=\"maincontent\">", "<!-- end content -->");
				for (String url : snip(mainContentDiv, "<a class=\"versionbutton release\"href=\"", "\">")) {
					int index = url.lastIndexOf('/');
					String version = url.substring(index + 1);
					walkVersion(baseUrl, projectName, version, url, visitor);
				}
				visitor.finishedProject(baseUrl, tagName, projectName, projectUrl);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
	}

	private boolean shouldExit() {
		return maxProjects > 0 && count > maxProjects;
	}

	private void walkVersion(String baseUrl, String project, String version, String versionUrl, IMavenVisitor visitor) {
		try {
			String text = Files.getText(new URL(baseUrl + versionUrl));
			String mainContentDiv = onlySnip(text, "<div id=\"maincontent\">", "<!-- end content -->");
			String jarUrl = getItem(mainContentDiv, "<th>Artifact</th>", ">Download", "href=\"", "\"");
			String pomUrl = getItem(mainContentDiv, "<th>POM File</th>", ">View", "href=\"", "\"");
			List<String> packages = findPackages(mainContentDiv);
			String pom = getPom(pomUrl);
			visitor.version(baseUrl, project, version, versionUrl, jarUrl, pomUrl, pom, packages);
			System.out.println(versionUrl + " Jar: " + jarUrl);
			Thread.sleep(10);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private String getPom(String pomUrl) throws MalformedURLException {
		try {
			String pom = pomUrl.length() > 0 ? Files.getText(new URL(pomUrl)) : "";
			return pom;
		} catch (Exception e) {
			return "";
		}
	}

	private List<String> findPackages(String mainContentDiv) {
		try {
			String packageBlock = onlySnip(mainContentDiv, "<h3>Packages</h3>", "</table>");
			List<String> packages = snip(packageBlock, "<td>", "</td>");
			return packages;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	private String getItem(String mainContentDiv, String start1, String end1, String start2, String end2) {
		try {
			String jarBlock = onlySnip(mainContentDiv, start1, end1);
			String jarUrl = onlySnip(jarBlock, start2, end2);
			return jarUrl;
		} catch (Exception e) {
			return "";
		}
	}

	private String findMainContentDiv(String text) {
		String mainContentDiv = onlySnip(text, "<div id=\"maincontent\">", "</div>");
		return mainContentDiv;
	}

	private String onlySnip(String mainContentDiv, String start, String end) {
		List<String> result = snip(mainContentDiv, start, end);
		if (result.size() != 1)
			throw new IllegalStateException(result.toString());
		return result.get(0);

	}

	private List<String> snip(String mainContentDiv, String start, String end) {
		List<String> result = Lists.newList();
		int index = 0;
		while (true) {
			int startIndex = mainContentDiv.indexOf(start, index);
			if (startIndex == -1)
				return result;
			int endIndex = mainContentDiv.indexOf(end, startIndex + start.length() + 1);
			if (endIndex == -1)
				return result;
			result.add(mainContentDiv.substring(startIndex + start.length(), endIndex));
			index = endIndex;
		}
	}

	public static void main(String[] args) {
		new MavenRepoWalker(2).walkAllTags("http://mvnrepository.com", IMavenNavigator.Utils.sysoutNavigator());
	}

}
