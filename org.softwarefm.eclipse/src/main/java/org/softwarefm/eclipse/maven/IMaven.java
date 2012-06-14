package org.softwarefm.eclipse.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.link.IMakeLink;
import org.softwarefm.eclipse.maven.internal.Maven;
import org.softwarefm.eclipse.selection.FileNameAndDigest;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Files;

public interface IMaven {

	Model pomToModel(String pomUrl) throws Exception;

	File downloadJar(Model model) throws Exception;

	URL jarUrl(Model model) throws MalformedURLException;

	File jarFile(Model model);

	public static class Utils {
		public static IMaven makeImport() {
			return new Maven();
		}

		public static String makePomUrlForMvnRepository(String groupId, String artifactId, String version) {
			return "http://repo1.maven.org/maven2/" + groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".pom";
		}

		public static IMaven makeImport(File m2Home) {
			return new Maven(m2Home);
		}

		public static String getName(Model model) {
			String name = model.getName();
			if (name != null)
				return name;
			return getArtifactId(model);
		}

		public static String getGroupId(Model model) {
			String groupId = model.getGroupId();
			if (groupId != null)
				return groupId;
			Parent parent = model.getParent();
			return parent.getGroupId();
		}

		public static String getArtifactId(Model model) {
			String groupId = model.getArtifactId();
			if (groupId != null)
				return groupId;
			Parent parent = model.getParent();
			return parent.getArtifactId();
		}

		public static String getVersion(Model model) {
			String groupId = model.getVersion();
			if (groupId != null)
				return groupId;
			Parent parent = model.getParent();
			return parent.getVersion();
		}

		public static ICallback<String> importPomWithSysouts(final IMakeLink makeLink) {
			final Maven maven = new Maven();
			return new ICallback<String>() {
				public void process(String pomUrl) throws Exception {
					System.out.println("Downloading pom: " + pomUrl);
					Model model = maven.pomToModel(pomUrl);
					System.out.println("Loaded pom");
					System.out.println("Jar url: " + maven.jarUrl(model));
					File jarFile = maven.jarFile(model);
					System.out.println("Target: " + jarFile);
					maven.downloadJar(model);
					System.out.println("Imported to local repository");
					FileNameAndDigest fileNameAndDigest = new FileNameAndDigest(jarFile, Files.digestAsHexString(jarFile));
					makeLink.makeDigestLink(new ProjectData(fileNameAndDigest, IMaven.Utils.getGroupId(model), IMaven.Utils.getArtifactId(model), IMaven.Utils.getVersion(model)));
				}
			};
		}

		public static String getIssueManagementUrl(Model model) {
			IssueManagement issueManagement = model.getIssueManagement();
			if (issueManagement == null)
				return null;
			else
				return issueManagement.getUrl();
		}
	}

}
