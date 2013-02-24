package org.softwarefm.mavenRip.graph;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.softwarefm.utilities.callbacks.ICallback;

public class Neo4JFakeData {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		final INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService t) throws Exception {
				Node g1a1v111 = neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "1.1.1", "pomUrl", "pom", "digest1");
				Node g1a1v112 = neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "1.1.2", "pomUrl", "pom", "digest1");
				Node g2a1v111 = neo4Sfm.addGroupArtifactVersionDigest("g2", "a1", "1.1.1", "pomUrl", "pom", "digest1");
				Node g3a1v111 = neo4Sfm.addGroupArtifactVersionDigest("g3", "a1", "1.1.1", "pomUrl", "pom", "digest1");
				neo4Sfm.addVersionDependency(g1a1v112, g2a1v111);
				neo4Sfm.addVersionDependency(g1a1v112, g3a1v111);
			}
		});
	}
}
