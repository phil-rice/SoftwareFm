package org.softwarefm.mavenRip.graph;

import org.neo4j.graphdb.Node;

public class Neo4JGroupArtifactVersionDigestAdapter implements INeo4JGroupArtifactVersionDigestVistor{

	@Override
	public void accept(Node groupNode) {
	}

	@Override
	public void accept(Node groupNode, Node artifactNode) {
	}

	@Override
	public void accept(Node groupNode, Node artifactNode, Node versionNode) {
	}

	@Override
	public void accept(Node groupNode, Node artifactNode, Node versionNode, Node digestNode) {
	}

}
