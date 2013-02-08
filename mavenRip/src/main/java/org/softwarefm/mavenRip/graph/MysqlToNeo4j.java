package org.softwarefm.mavenRip.graph;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.mavenRip.MavenRip;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Files;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class MysqlToNeo4j {

	public static void main(String[] args) {
		final INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");

		final IMaven maven = IMaven.Utils.makeImport(new File("mavenRip"));
		final JdbcTemplate template = new JdbcTemplate(MavenRip.dataSource);
		template.query("select * from maven where digest is not null and digest <> 'failed' and pom is null", new RowCallbackHandler() {
			@Override
			public void processRow(final ResultSet rs) throws SQLException {
				try {
					neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
						@Override
						public void process(GraphDatabaseService t) throws Exception {
							String groupId = rs.getString("groupId");
							String artifactId = rs.getString("artifactId");
							String version = rs.getString("version");
							String digest = rs.getString("digest");

							String pomUrl = rs.getString("pomUrl");
							String pomText = Files.getTextFromUrl(pomUrl);
							maven.pomStringToModel(pomText, "UTF-8"); // just make sure we can parse it

							template.update("update maven set pom=? where pomUrl=?", pomText, pomUrl);
							Node node = neo4Sfm.addGroupArtifactVersionDigest(groupId, artifactId, version, pomUrl, pomText, digest);
							System.out.println("Done: " + node.getProperty(Neo4SfmConstants.fullIdProperty));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
