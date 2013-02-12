package org.softwarefm.mavenRip.graph;

public interface INeo4JGroupArtifactVersionDigestWalker {

	void accept(INeo4JGroupArtifactVersionDigestVistor visitor);

}