package org.softwarefm.mavenRip.graph;

import org.neo4j.graphdb.Node;

public interface INeo4JGroupArtifactVersionDigestVistor {

	void accept(Node groupNode);

	void accept(Node groupNode, Node artifactNode);

	void accept(Node groupNode, Node artifactNode, Node versionNode);

	void accept(Node groupNode, Node artifactNode, Node versionNode, Node digestNode);
}
