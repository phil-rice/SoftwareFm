package org.softwarefm.core.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.link.IMakeLink;
import org.softwarefm.core.maven.internal.Maven;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.utilities.callbacks.ICallback2;
import org.softwarefm.utilities.collections.Files;

public interface IMaven {

	Model pomToModel(String pomUrl) ;

	File downloadJar(Model model) ;

	URL jarUrl(Model model) throws MalformedURLException;

	File jarFile(Model model);

	String groupId(Model model);

	String artifactId(Model model);

	String version(Model model);

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

		public static ICallback2<String, Integer> importPomWithSysouts(final IMakeLink makeLink, final ISelectedBindingManager<?> manager) {
			final Maven maven = new Maven();
			return new ICallback2<String, Integer>() {
				public void process(String pomUrl, Integer thisSelectionId) throws Exception {
					System.out.println("Downloading pom: " + pomUrl);
					Model model = maven.pomToModel(pomUrl);
					System.out.println("Loaded pom");
					System.out.println("Jar url: " + maven.jarUrl(model));
					File jarFile = maven.jarFile(model);
					System.out.println("Target: " + jarFile);
					maven.downloadJar(model);
					System.out.println("Imported to local repository");
					FileAndDigest fileAndDigest = new FileAndDigest(jarFile, Files.digestAsHexString(jarFile));
					makeLink.makeDigestLink(new ArtifactData(fileAndDigest, IMaven.Utils.getGroupId(model), IMaven.Utils.getArtifactId(model), IMaven.Utils.getVersion(model)));
					manager.reselect(thisSelectionId);

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
