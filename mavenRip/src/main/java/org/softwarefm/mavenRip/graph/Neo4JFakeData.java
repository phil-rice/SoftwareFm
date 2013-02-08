package org.softwarefm.mavenRip.graph;

import org.neo4j.graphdb.Node;

public class Neo4JFakeData {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
		Node g1a1v111 = neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "1.1.1", "pomUrl", "pom", "digest1");
		Node g1a1v112 = neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "1.1.2", "pomUrl", "pom", "digest1");
		Node g2a1v111 = neo4Sfm.addGroupArtifactVersionDigest("g2", "a1", "1.1.1", "pomUrl", "pom", "digest1");
		Node g3a1v111 = neo4Sfm.addGroupArtifactVersionDigest("g3", "a1", "1.1.1", "pomUrl", "pom", "digest1");
		INeo4Sfm.Utils.addDependency(INeo4Sfm.Utils.getArtifactFromVersion(g1a1v112), INeo4Sfm.Utils.getArtifactFromVersion(g2a1v111));
		INeo4Sfm.Utils.addDependency(INeo4Sfm.Utils.getArtifactFromVersion(g1a1v112), INeo4Sfm.Utils.getArtifactFromVersion(g3a1v111));
	}
}
