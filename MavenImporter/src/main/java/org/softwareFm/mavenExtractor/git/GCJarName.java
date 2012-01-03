package org.softwareFm.mavenExtractor.git;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.server.GitRepositoryFactory;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class GCJarName  {

	private final static IGitFacard gitFacard = IGitFacard.Utils.makeFacard();

	public static void main(String[] args) {
		// IRepositoryFacard slingRepository = IRepositoryFacard.Utils.frontEnd("178.79.180.172", 8080, "admin", "admin");
		File home = new File(System.getProperty("user.home"));
		// File localRoot = new File(home, ".sfm");
		final File remoteRoot = new File(home, ".sfm_remote");
		// IRepositoryFacard repository = GitRepositoryFactory.gitLocalRepositoryFacard("localhost", 8080, localRoot, remoteRoot);
		IRepositoryFacard repository = GitRepositoryFactory.forImport(remoteRoot);
		final IUrlGenerator urlGenerator = ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(CardConstants.softwareFmPrefix, CardConstants.dataPrefix).get(CardConstants.jarNameUrlKey);
		new JdbcTemplate(MavenImporterConstants.dataSource).query("select * from jar", new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				String jarName = rs.getString("name");
				String artifactId = rs.getString("artifactId");
				String url = urlGenerator.findUrlFor(Maps.stringObjectMap(CardConstants.jarName, artifactId));
				String repoUrl = Strings.allButLastSegment(url, "/");
				File repoDirectory = new File(remoteRoot, repoUrl);
				gitFacard.gc(remoteRoot, url);
			}
		});
	}
}