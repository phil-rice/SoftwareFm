package org.softwarefm.mavenRip.graph;

import java.text.MessageFormat;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.TraversalDescription;

public class Neo4JWalker {
	public static void main(String[] args) {
		INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
		final Node groupReference = neo4Sfm.graphReference();
		TraversalDescription description = INeo4Sfm.Utils.groupIdArtifactVersionDigest();

		for (Path path : description.traverse(groupReference)) {
			// System.out.println(path);
			Node endNode = path.endNode();
			print(endNode);
			for (Relationship relation : endNode.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.DEPENDS_ON)) {
				Node dependant = relation.getEndNode();
				print("  Depends on {0}", Neo4SfmConstants.groupArtifactidProperty, dependant);
				print("    Depends on {0}", Neo4SfmConstants.fullIdProperty, dependant);
			}
		}
	}

	public static void print(Node endNode) {
		print("{0}", Neo4SfmConstants.groupIdProperty, endNode);
		print("  {0}", Neo4SfmConstants.artifactIdProperty, endNode);
		print("    {0}", Neo4SfmConstants.versionProperty, endNode);
		print("      {0}", Neo4SfmConstants.digestProperty, endNode);
	}

	private static void print(String pattern, String property, Node node) {
		if (node.hasProperty(property))
			System.out.println(MessageFormat.format(pattern, node.getProperty(property)));
	}

}
