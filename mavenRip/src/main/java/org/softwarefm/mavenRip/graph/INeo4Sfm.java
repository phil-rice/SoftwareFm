package org.softwarefm.mavenRip.graph;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.softwarefm.mavenRip.graph.internal.Neo4Sfm;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.functions.IFunction1;

public interface INeo4Sfm extends INeo4JGroupArtifactVersionDigestWalker {

	Node graphReference();

	/** returns a node that is connected to firstNode by relationship, and has the specified property and value */
	Node findOrCreate(Node firstNode, SoftwareFmRelationshipTypes relationShip, String property, String value);

	Node find(String groupId, String artifactId, String version);

	/** Returns the version node */
	Node addGroupArtifactVersionDigest(String groupId, String artifactId, String version, String pomUrl, String pom, String digest);

	void addDependency(final Node parentVersionNode, final Node dependencyVersionNode);

	void execute(ICallback<GraphDatabaseService> callback);

	<T> T execute(IFunction1<GraphDatabaseService, T> fn);

	Node getNodeOrNull(Index<Node> index, String property, Object value);

	boolean hasRedundantRelations(Node from);

	void removeRedundantRelations(Node from);

	public static class Utils {

		public static INeo4Sfm neo4Sfm(String root) {
			final GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(root);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					graphDb.shutdown();
				}
			});
			return new Neo4Sfm(graphDb);
		}

		public static INeo4Sfm neo4Sfm(GraphDatabaseService graphDb) {
			return new Neo4Sfm(graphDb);
		}

		public static String makeFullId(String groupId, String artifactId, String version) {
			return groupId + ":" + artifactId + ":" + version;
		}

		public static TraversalDescription groupIdArtifact() {
			TraversalDescription description = Traversal.description().//
					depthFirst().//
					relationships(SoftwareFmRelationshipTypes.HAS_GROUP, Direction.OUTGOING).//
					relationships(SoftwareFmRelationshipTypes.HAS_ARTIFACT, Direction.OUTGOING).//
					uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);
			return description;
		}

		public static TraversalDescription groupIdArtifactVersion() {
			TraversalDescription description = groupIdArtifactVersion().//
					relationships(SoftwareFmRelationshipTypes.HAS_VERSION, Direction.OUTGOING);
			return description;
		}

		public static TraversalDescription groupIdArtifactVersionDigest() {
			TraversalDescription description = groupIdArtifactVersion().//
					relationships(SoftwareFmRelationshipTypes.HAS_DIGEST, Direction.OUTGOING);
			return description;
		}

		public static Node getArtifactFromVersion(Node versionNode) {
			Node artifactNode = Iterables.getOnly(versionNode.getRelationships(SoftwareFmRelationshipTypes.HAS_VERSION, Direction.INCOMING)).getStartNode();
			return artifactNode;

		}

		public static Node getGroupFromArtifact(Node artifactNode) {
			Node groupNode = Iterables.getOnly(artifactNode.getRelationships(SoftwareFmRelationshipTypes.HAS_ARTIFACT, Direction.INCOMING)).getStartNode();
			return groupNode;
		}

		public static String makeGroupArtifactId(String groupId, String artifactId) {
			return groupId + ":" + artifactId;
		}

	}

}
