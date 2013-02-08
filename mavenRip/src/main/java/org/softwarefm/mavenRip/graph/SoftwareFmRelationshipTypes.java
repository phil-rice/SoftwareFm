package org.softwarefm.mavenRip.graph;

import org.neo4j.graphdb.RelationshipType;

public enum SoftwareFmRelationshipTypes implements RelationshipType {
	HAS_GROUP, HAS_ARTIFACT, HAS_VERSION, HAS_DIGEST, DEPENDS_ON;
}
