package org.softwarefm.mavenRip.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.maven.model.Model;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.mavenRip.IGroupArtifactVisitor;
import org.softwarefm.mavenRip.IMavenDigestVistor;
import org.softwarefm.mavenRip.IMavenDigestWalker;
import org.softwarefm.mavenRip.IMavenWalker;
import org.softwarefm.mavenRip.MavenRip;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.strings.Strings;
import org.springframework.jdbc.core.JdbcTemplate;

public class HighestVersionDigestWalker implements IMavenDigestWalker {

	private final JdbcTemplate template;
	private final IMavenWalker walker;
	private final IMaven maven;
	private final boolean onlyVisitNew;

	public HighestVersionDigestWalker(IMaven maven, DataSource dataSource, ICallback<Throwable> errorCallback, boolean onlyVisitNew) {
		this.maven = maven;
		this.onlyVisitNew = onlyVisitNew;
		this.template = new JdbcTemplate(dataSource);
		this.walker = IMavenWalker.Utils.walker(dataSource, errorCallback);
	}

	@Override
	public void walk(final IMavenDigestVistor visitor) {
		walker.walk(new IGroupArtifactVisitor() {
			@Override
			public void visit(String groupId, String artifactId, Map<String, String> versionToPomUrl) {
				ArrayList<String> versions = new ArrayList<String>(versionToPomUrl.keySet());
				Collections.sort(versions, Strings.invertedVersionComparator);
				String highestVersion = versions.get(0);
				String pomUrl = versionToPomUrl.get(highestVersion);
				Model model = maven.pomToModel(pomUrl);
				List<String> existingDigest = template.queryForList("select digest from maven where pomUrl = ?", String.class, pomUrl);
				File jarFile = maven.jarFile(model);
				String digest = existingDigest.get(0);
				if (digest == null || digest.length() == 0) {
					File file = maven.downloadJar(model);
					digest = Files.digestAsHexString(file);
					template.update("update maven set digest =? where pomUrl = ?", digest, pomUrl);
					visitor.visit(pomUrl, model, groupId, artifactId, highestVersion, jarFile, digest);
				} else if (!onlyVisitNew)
					visitor.visit(pomUrl, model, groupId, artifactId, highestVersion, jarFile, digest);

			}

		});
	}

	public static void main(String[] args) {
		ICallback<Throwable> errorCallback = ICallback.Utils.sysErrCallback();
		HighestVersionDigestWalker walker = new HighestVersionDigestWalker(IMaven.Utils.makeImport(new File("localMavenDownload")), MavenRip.dataSource, errorCallback, false);
		walker.walk(new IMavenDigestVistor() {
			@Override
			public void visit(String pomUrl, Model model, String groupId, String artifactId, String version, File file, String hexDigest) {
				System.out.println(hexDigest + "   " + pomUrl + " " + file);
				Assert.assertTrue(file.exists());
			}
		});
	}
}
