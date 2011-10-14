package org.softwareFm.configuration;

import java.io.File;

public class ConfigurationConstants {
	public static final String editorSfmIdKey = "editor.softwareFm.id";
	public static final String editorTextKey = "editor.text";
	public static final String editorJarKey = "editor.jar";

	public static final String editorStyledTextKey = "editor.styled.text";
	public static final String editorJavadocKey = "editor.javadoc";
	public static final String editorSourceKey = "editor.source";

	public static final String blankKey = "blank.title";
	public static final String jarPathTitle = "jar.jarPath.title";
	public static final String jarNameTitle = "jar.jarName.title";
	public static final String jarHexDigestTitle = "jar.hexDigest.title";

	public static final String softwareFmIdTitle = "jar.softwareFmId.title";
	public static final String versionTitle = "jar.version.title";
	public static final String artifactIdTitle = "jar.artifactId.title";
	public static final String groupIdTitle = "jar.groupId.title";

	public static final String artifactJobTitle = "artifact.job.title";
	public static final String dataArtifactJob = "data.artifact.job";

	public static final String artifactUrlTitle = "artifact.url.title";
	public static final String artifactJavadocTitle = "artifact.javadoc.title";
	public static final String artifactSourceTitle = "artifact.source.title";

	public static final String artifactNameTitle = "artifact.name.title";
	public static final String artifactDescriptionTitle = "artifact.description.title";
	public static final String artifactIssuesTitle = "artifact.issues.title";
	public static final String artifactIssuesTooltip = "artifact.issues.tooltip";
	public static final String artifactMailingListTitle = "artifact.mailingList.title";
	public static final String artifactTutorialsTitle = "artifact.tutorials.title";
	public static final String artifactBlogsTitle = "artifact.blogs.title";

	public static final String artifactRssAdd = "artifact.rss.add";
	public static final String artifactTwitterAdd = "artifact.twitter.add";
	public static final String artifactFacebookAdd = "artifact.facebook.add";

	public static final String groupNameTitle = "group.name.title";
	public static final String groupVersionTitle = "group.version.title";
	public static final String groupDescriptionTitle = "group.description.title";

	public static final String dataGroupName = "data.group.name";
	public static final String groupRssAdd = "group.rss.add";
	public static final String groupTwitterAdd = "group.twitter.add";

	public static final String dataRawHexDigest = "data.raw.jar.hexDigest";
	public static final String dataRawJarName = "data.raw.jar.jarName";
	public static final String dataRawJarPath = "data.raw.jar.jarPath";
	public static final String dataRawJavadoc = "data.raw.jar.javadoc";
	public static final String dataRawSource = "data.raw.jar.source";
	public static final String dataRawJavadocMutator = "data.raw.jar.javadocMutator";
	public static final String dataRawSourceMutator = "data.raw.jar.sourceMutator";

	public static final String dataJarArtifactId = "data.jar.artifactId";
	public static final String dataJarGroupId = "data.jar.groupId";
	public static final String dataJarVersion = "data.jar.version";
	public static final String dataJarJavadoc = "data.jar.javadoc";
	public static final String dataJarSource = "data.jar.source";

	public static final String dataArtifactUrl = "data.artifact.url";
	public static final String dataArtifactName = "data.artifact.name";
	public static final String dataArtifactDescription = "data.artifact.description";
	public static final String dataArtifactIssues = "data.artifact.issues";

	public static final String dataArtifactBlogs = "data.artifact.project.blogs";
	public static final String dataArtifactMailingList = "data.artifact.mailingLists";
	public static final String dataArtifactRss = "data.artifact.rss";
	public static final String dataArtifactTutorials = "data.artifact.tutorials";
	public static final String dataArtifactTweets = "data.artifact.tweets";
	public static final String dataArtifactFacebook = "data.artifact.facebook";

	public static final String dataGroupRss = "data.group.rss";
	public static final String dataGroupTweets = "data.group.tweets";
	public static final String dataGroupFacebook = "data.group.facebook";

	public static final String hexDigestMissingTitle = "hexDigest.missing.title";
	public static final String artifactNameMissingTitle = "artifact.name.missing.title";
	public static final String softwareFmIdMissingTitle = "jar.softwareFmId.missing.title";
	public static final String groupNameMissingTitle = "group.name.missing.title";

	public static final String artifactUrlMissingTitle = "artifact.url.missing.title";
	public static final String descriptionOfNoneJarFile = "description.noneJarFile.text";
	public static final String descriptionWhenIdsNotDefined = "description.idsNotDefined.text";
	public static final String expectedRippedResult = "Expected a RippedResult. Value = {0}. Class = {1)";

	public static final String primaryEntity = "jar";
	public static final String artifactId = "artifactId";
	
	public static final String groupId = "groupId";
	public static final String version = "version";
	
	public static final String entityForPlayList = "artifact";
	public static final String entityForJavadocSource = "jar";
	
	public static final String sfmIdPattern = "id.sfm.pattern";

	public static final String buttonCopyToEclipseTitle = "button.copyToEclipse.title";
	public static final String buttonCopyToSoftwareFmTitle = "button.copyToSoftwareFm.title";
	public static final String buttonCopyToBothTitle = "button.copyToBoth.title";
	public static final String buttonCopyEclipseToSoftwareFmTitle = "button.copyEclipseToSoftwareFm.title";
	public static final String buttonTestTitle = "button.test.title";
	public static final String settingEclipseValue = "javadocSource.settingEclipseValue.text";
	public static final String setEclipseValue = "javadocSource.setEclipseValue.text";

	public static final File defaultDirectoryForDownloads = new File(System.getProperty("user.home"), "softwareFm");
	public static final String javadocKey = "javadoc.title";
	public static final String sourceKey = "source.title";

	public final static String urlJavadocTitle = "url.javadoc.title";
	public final static String urlSourceTitle = "url.source.title";

	public static final String groupReferenceTitle = "group.reference.title";
}
