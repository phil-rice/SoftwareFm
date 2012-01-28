package org.softwareFm.mavenExtractor.git;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.url.IUrlGenerator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class ImportJarName2 {

	public static void main(String[] args) {
		// IRepositoryFacard slingRepository = IRepositoryFacard.Utils.frontEnd("178.79.180.172", 8080, "admin", "admin");
		File home = new File(System.getProperty("user.home"));
		// File localRoot = new File(home, ".sfm");
		final File remoteRoot = new File(home, ".sfm_remote");
		// IRepositoryFacard repository = GitRepositoryFactory.gitLocalRepositoryFacard("localhost", 8080, localRoot, remoteRoot);
		final IUrlGenerator urlGenerator = ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(CardConstants.softwareFmPrefix, CardConstants.dataPrefix).get(CardConstants.jarNameUrlKey);
		final IGitFacard gitFacard = IGitFacard.Utils.makeFacard();
		JdbcTemplate template = new JdbcTemplate(MavenImporterConstants.dataSource);
		final AtomicInteger count = new AtomicInteger();
		template.query("select * from jar group by artifactId", new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				String artifactId = rs.getString("artifactId");
				String url = urlGenerator.findUrlFor(Maps.stringObjectMap(CardConstants.jarName, artifactId));
				String repoUrl = Strings.allButLastSegment(url, "/");
				File repoDirectory = new File(remoteRoot, repoUrl);
				File jarNameDirectory = new File(remoteRoot, url);
				jarNameDirectory.mkdirs();
				if (!new File(repoDirectory, CommonConstants.DOT_GIT).exists()) {
					gitFacard.createRepository(remoteRoot, repoUrl);
					System.out.println("Creating: " + repoDirectory);
				}
				
				File file = new File(jarNameDirectory, CommonConstants.dataFileName);
				String collectionData = Json.toString(Maps.stringObjectLinkedMap(CardConstants.slingResourceType, CardConstants.collection));
				Files.setText(file, collectionData);

				String uuid = UUID.randomUUID().toString();
				File itemDirectory = new File(jarNameDirectory, uuid);
				itemDirectory.mkdir();
				
				String groupId = rs.getString("groupId");
				String data = Json.toString(Maps.stringObjectLinkedMap(CardConstants.group, groupId, CardConstants.artifact, artifactId, CardConstants.slingResourceType, CardConstants.jarName));
				File itemFile = new File(itemDirectory, CommonConstants.dataFileName);
				Files.setText(itemFile, data);
				gitFacard.addAllAndCommit(remoteRoot, IFileDescription.Utils.plain(repoUrl), "Import Jar Name");
				System.out.println(String.format("%6d %-40s" ,count.incrementAndGet(), artifactId));
			}
		});
	}
}