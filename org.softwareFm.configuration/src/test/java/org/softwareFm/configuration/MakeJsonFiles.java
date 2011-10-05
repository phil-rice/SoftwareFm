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

	private final static String javadocJar = "http://someJavadocUrl.jar";
	private final static String javadocUrl = "http://someJavadocUrl.html";

	private final static Map<String, Object> ripped = Maps.makeMap(//
			"hexDigest", "somedigest",//
			"jarName", "jarName.java",//
			"jarPath", "a\\b\\jarPath.java",//
			"javadoc", javadocJar,//
			"source", "someSourceUrl");

	private final static Map<String, Object> artifact = Maps.makeMap(//
			"artifactId", "RunTime",//
			"groupId", "oracle.com",//
			"version", 24,//
			"name", "Java Language main Runtime Jar",//
			"url", "http://www.oracle.com/us/technologies/java/index.html",//
			"javadoc", javadocJar,//
			"source", "http://someSource/url.jar",//
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
				for (boolean javadocIsJar : Booleans.falseTrue)
					for (boolean eclipseAndSoftwareFmMatch : Booleans.falseTrue)
						makeJavadocSource(eclipse, softwareFm, javadocIsJar, eclipseAndSoftwareFmMatch);
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

	private static void makeJavadocSource(boolean eclipse, boolean softwareFm, boolean javadocIsJar, boolean eclipseAndSoftwareFmMatch) {
		if (!eclipseAndSoftwareFmMatch)
			if (!eclipse || !softwareFm)
				return;
		String pattern = "Javadoc{0}{1}Source{0}{1}isJar{2}match{3}.json";
		String fileName = MessageFormat.format(pattern, Booleans.toTf(eclipse), Booleans.toTf(softwareFm), Booleans.toTf(javadocIsJar), Booleans.toTf(eclipseAndSoftwareFmMatch));
		Map<String, Object> localRipped = Maps.copyMap(ripped);
		Map<String, Object> localArtifact = Maps.copyMap(artifact);
		if (javadocIsJar) {
			localRipped.put("javadoc", javadocUrl);
			localArtifact.put("javadoc", javadocUrl);
		}
		if (!eclipseAndSoftwareFmMatch) {
			localArtifact.put("javadoc", "softwareFmValue.jar");
			localArtifact.put("source", "softwareFmValue.jar");
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
