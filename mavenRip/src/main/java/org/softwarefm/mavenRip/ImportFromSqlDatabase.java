package org.softwarefm.mavenRip;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.maven.model.Model;
import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.link.IMakeLink;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.core.selection.FileAndDigest;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.utilities.collections.Files;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class ImportFromSqlDatabase {

	public static void main(String[] args) {
		System.out.println("Import from Sql database");

		final IMaven maven = IMaven.Utils.makeImport(new File("mavenRip"));
		IArtifactDataCache projectDataCache = IArtifactDataCache.Utils.noCache();
		final IMakeLink makeLink = IMakeLink.Utils.makeLink(IUrlStrategy.Utils.urlStrategy(), projectDataCache);
		BasicDataSource datasource = MavenRip.dataSource;
		final JdbcTemplate template = new JdbcTemplate(datasource);
		template.query("select * from maven where digest is null or digest = ''", new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				try {
					String groupId = rs.getString("groupId");
					String artifactId = rs.getString("artifactId");
					String version = rs.getString("version");
					String pomUrl = rs.getString("pomUrl");
					Model model = maven.pomToModel(pomUrl);
					File file = maven.downloadJar(model);
					String digest = Files.digestAsHexString(file);
					ArtifactData artifactData = new ArtifactData(new FileAndDigest(file, digest), groupId, artifactId, version);
					System.out.println(artifactData);
					template.update("update maven set digest =? where pomUrl = ?", digest, pomUrl);
					makeLink.makeDigestLink(artifactData);
					makeLink.populateProjectIfBlank(artifactData, model);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
