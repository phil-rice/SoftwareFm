package org.softwareFm.mavenExtractor.git;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.mavenExtractor.MavenImporterConstants;
import org.softwareFm.server.IGitFacard;
import org.softwareFm.utilities.maps.Maps;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class GCJarName  {

	private final static IGitFacard gitFacard = IGitFacard.Utils.makeFacard();

	public static void main(String[] args) {
		File home = new File(System.getProperty("user.home"));
		final File remoteRoot = new File(home, ".sfm_remote");
		final IUrlGenerator urlGenerator = ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap(CardConstants.softwareFmPrefix, CardConstants.dataPrefix).get(CardConstants.jarNameUrlKey);
		new JdbcTemplate(MavenImporterConstants.dataSource).query("select * from jar", new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				String artifactId = rs.getString("artifactId");
				String url = urlGenerator.findUrlFor(Maps.stringObjectMap(CollectionConstants.artifactId, artifactId));
				gitFacard.gc(remoteRoot, url);
			}
		});
	}
}