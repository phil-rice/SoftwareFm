package org.softwareFm.configuration;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

import org.json.simple.JSONValue;
import org.softwareFm.utilities.booleans.Booleans;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.maps.Maps;

public class MakeJsonFiles {
	private final static File root = new File("../org.softwareFm.configuration/src/test/resources/org/softwareFm/configuration");

	private final static String javadocJar = "someJavadocUrl.jar";
	private final static String javadocJar2 = "someOtherJavadocUrl.jar";
	private final static String javadocUrl = "someJavadocUrl.html";
	private final static String javadocUrl2 = "someOtherJavadocUrl.html";
	private final static String sourceJar = "someSourceUrl.jar";
	private final static String sourceJar2 = "someOtherSourceUrl.jar";
	private final static String sourceUrl = "someSourceUrl.html";
	private final static String sourceUrl2 = "someOtherSourceUrl.html";

	private final static Map<String, Object> ripped = Maps.makeMap(//
			"hexDigest", "somedigest",//
			"jarName", "jarName.java",//
			"jarPath", "a\\b\\jarPath.java",//
			"source", "someSourceUrl");

	private final static Map<String, Object> artifact = Maps.makeMap(//
			"artifactId", "RunTime",//
			"groupId", "oracle.com",//
			"version", 24,//
			"name", "Java Language main Runtime Jar",//
			"url", "http://www.oracle.com/us/technologies/java/index.html",//
			"javadoc", javadocJar,//
			"mailingLists", Arrays.asList("a$b"),//
			"issues", "http://bugs.sun.com/bugdatabase/index.jsp",//
			"tweets", Arrays.asList("tweet1", "tweet2"),//
			"rss", Arrays.asList("someRssFeed"),//
			"tutorials", Arrays.asList("tut1", "tut2"),//
			"blogs", Arrays.asList("blog1", "blog2"));

	private final static Map<String, Object> group = Maps.makeMap(//
			"name", "Oracle",//
			"tweets", Arrays.asList("tweetGrp1", "tweetGrp2"),//
			"rss", Arrays.asList("someGrpRssFeed"),//
			"blogs", Arrays.asList("blogGrp1", "blogGrp2"));

	public static void main(String[] args) {
		for (boolean eclipse : Booleans.falseTrue) {
			makeJavaFile(eclipse);
			for (boolean softwareFm : Booleans.falseTrue)
				for (boolean http : Booleans.falseTrue) {
					makeJavadocSource(eclipse, softwareFm, http);
					if (eclipse && softwareFm)
						makeJavadocSource(eclipse, softwareFm, http, true);
				}
		}
		for (boolean artifactId : Booleans.falseTrue)
			for (boolean artifactName : Booleans.falseTrue)
				for (boolean groupId : Booleans.falseTrue)
					makeFileForArtifactGroup(artifactId, groupId, artifactName);
		for (boolean groupId : Booleans.falseTrue)
			for (boolean groupName : Booleans.falseTrue)
				makeFileForGroup(groupId, groupName);

		makeFile("oracle.json", ripped, artifact, group);
	}

	private static void makeFileForGroup(boolean groupId, boolean groupName) {
		String pattern = "groupIdName{0}{1}.json";
		String fileName = MessageFormat.format(pattern, Booleans.toTf(groupId), Booleans.toTf(groupName));
		Map<String, Object> localRipped = Maps.copyMap(ripped);
		Map<String, Object> localArtifact = Maps.copyMap(artifact);
		Map<String, Object> localGroup = Maps.copyMap(group);
		if (!groupId)
			localArtifact.remove("groupId");
		if (!groupName)
			localGroup.remove("name");
		makeFile(fileName, localRipped, localArtifact, localGroup);

	}

	private static void makeJavaFile(boolean eclipse) {
		Map<String, Object> localRipped = Maps.with(ripped, "jarPath", "a\\b\\jarpath.java");
		if (!eclipse) {
			localRipped.remove("javadoc");
			localRipped.remove("source");
		}
		String fileName = MessageFormat.format("JavaFileSelected{0}.json", Booleans.toTf(eclipse));
		makeFile(fileName, localRipped, Maps.<String, Object> newMap(), Maps.<String, Object> newMap());
	}

	private static void makeFileForArtifactGroup(boolean artifactId, boolean groupId, boolean artifactName) {
		String pattern = "artifactId{0}GroupId{1}Name{2}.json";
		String fileName = MessageFormat.format(pattern, Booleans.toTf(artifactId), Booleans.toTf(groupId), Booleans.toTf(artifactName));
		Map<String, Object> localRipped = Maps.copyMap(ripped);
		Map<String, Object> localArtifact = Maps.copyMap(artifact);
		if (!artifactId)
			localArtifact.remove("artifactId");
		if (!groupId)
			localArtifact.remove("groupId");
		if (!artifactName)
			localArtifact.remove("name");
		makeFile(fileName, localRipped, localArtifact, group);

	}

	private static void makeJavadocSource(boolean eclipse, boolean softwareFm, boolean http) {
		makeJavadocSource(eclipse, softwareFm, http, false);
	}

	private static void makeJavadocSource(boolean eclipse, boolean softwareFm, boolean http, boolean match) {
		String pattern = "Javadoc{0}{1}Source{0}{1}Http{2}match{3}.json";
		String fileName = MessageFormat.format(pattern, Booleans.toTf(eclipse), Booleans.toTf(softwareFm), Booleans.toTf(http), Booleans.toTf(match));
		Map<String, Object> localRipped = Maps.copyMap(ripped);
		Map<String, Object> localArtifact = Maps.copyMap(artifact);
		if (http) {
			localRipped.put("javadoc", javadocJar);
			localRipped.put("source", sourceJar);
			if (match) {
				localArtifact.put("javadoc", javadocJar);
				localArtifact.put("source", sourceJar);
			} else {
				localArtifact.put("javadoc", javadocJar2);
				localArtifact.put("source", sourceJar2);

			}
		} else {
			localRipped.put("javadoc", javadocUrl);
			localRipped.put("source", sourceUrl);
			if (match) {
				localArtifact.put("javadoc", javadocUrl);
				localArtifact.put("source", sourceUrl);
			} else {
				localArtifact.put("javadoc", javadocUrl2);
				localArtifact.put("source", sourceUrl2);
			}

		}
		if (http) {
			localRipped.put("javadoc", "http://" + localRipped.get("javadoc"));
			localArtifact.put("javadoc", "http://" + localArtifact.get("javadoc"));

			localRipped.put("source", "http://" + localRipped.get("source"));
			localArtifact.put("source", "http://" + localArtifact.get("source"));
		}
		if (!eclipse) {
			localRipped.remove("javadoc");
			localRipped.remove("source");
		}
		if (!softwareFm) {
			localArtifact.remove("javadoc");
			localArtifact.remove("source");
		}
		makeFile(fileName, localRipped, localArtifact, group);
	}

	private static void makeFile(String fileName, Map<String, Object> localRipped, Map<String, Object> localArtifact, Map<String, Object> localGroup) {
		File file = new File(root, fileName);
		file.delete();
		Map<String, Object> data = Maps.makeMap("ripped", localRipped, "artifact", localArtifact, "group", localGroup);
		String text = JSONValue.toJSONString(data).replaceAll(",", ",\n");
		Files.setText(file, text);
	}

}
