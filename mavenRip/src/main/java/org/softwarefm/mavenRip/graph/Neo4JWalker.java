package org.softwarefm.mavenRip.graph;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.TraversalDescription;

public class Neo4JWalker {
	public static void main(String[] args) {
		INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("d:\\neo4j");
		final Node groupReference = neo4Sfm.graphReference();
		TraversalDescription description = INeo4Sfm.Utils.groupIdArtifactVersionDigest();

		for (Path path : description.traverse(groupReference)) {
			// System.out.println(path);
			Node endNode = path.endNode();
			INeo4Sfm.Utils.print(endNode);
			for (Relationship relation : endNode.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.DEPENDS_ON)) {
				Node dependant = relation.getEndNode();
				INeo4Sfm.Utils.print("  Depends on {0}", Neo4SfmConstants.groupArtifactidProperty, dependant);
				INeo4Sfm.Utils.print("    Depends on {0}", Neo4SfmConstants.fullIdProperty, dependant);
			}
		}
	}

}
