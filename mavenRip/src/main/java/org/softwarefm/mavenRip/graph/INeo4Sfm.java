package org.softwarefm.mavenRip.graph;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.softwarefm.mavenRip.graph.internal.Neo4Sfm;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.functions.IFunction1;

public interface INeo4Sfm extends INeo4JGroupArtifactVersionDigestWalker {

	Node graphReference();

	/** returns a node that is connected to firstNode by relationship, and has the specified property and value */
	Node findOrCreate(Node firstNode, SoftwareFmRelationshipTypes relationShip, String property, String value);

	Node find(String groupId, String artifactId);

	Node find(String groupId, String artifactId, String version);

	/**
	 * Returns the version node
	 * 
	 * @param digest2
	 */
	Node addGroupArtifactVersionDigest(String groupId, String artifactId, String version, String pomUrl, String pomText, String digest);

	void addArtifactDependency(final Node parentArtifactNode, final Node dependencyArtifactNode);

	void addVersionDependency(final Node parentVersionNode, final Node dependencyVersionNode);

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
			TraversalDescription description = groupIdArtifact().//
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

		public static final IFunction1<Relationship, Node> endNodeFn = new IFunction1<Relationship, Node>() {
			@Override
			public Node apply(Relationship from) throws Exception {
				return from.getEndNode();
			}

			@Override
			public String toString() {
				return "endNodeFn";
			}

		};

		public static final IFunction1<Relationship, Node> startNodeFn = new IFunction1<Relationship, Node>() {
			@Override
			public Node apply(Relationship from) throws Exception {
				return from.getStartNode();
			}

			@Override
			public String toString() {
				return "startNodeFn";
			}
		};

		public static final IFunction1<Node, String> node2groupIdArtifactId = new IFunction1<Node, String>() {
			@Override
			public String apply(Node from) throws Exception {
				try {
					return (String) from.getProperty(Neo4SfmConstants.groupArtifactidProperty);
				} catch (Exception e) {
					System.err.println("node2GroupIdArtifactId");
					INeo4Sfm.Utils.print(from);
					throw WrappedException.wrap(e);
				}
			}
		};

		public static final IFunction1<Node, String> node2groupIdArtifactVersionId = new IFunction1<Node, String>() {
			@Override
			public String apply(Node from) throws Exception {
				return (String) from.getProperty(Neo4SfmConstants.fullIdProperty);
			}
		};

		public static List<Node> getDependents(Node node, Direction direction) {
			IFunction1<Relationship, Node> whichNode = direction.equals(Direction.INCOMING) ? startNodeFn : endNodeFn;
			List<Node> dependants = new ArrayList<Node>();
			for (Relationship relation : node.getRelationships(direction, SoftwareFmRelationshipTypes.DEPENDS_ON)) {
				Node other = IFunction1.Utils.apply(whichNode, relation);
				if (other == null)
					throw new NullPointerException();
				dependants.add(other);
			}
			return dependants;
		}

		public static void print(Node endNode) {
			print("{0}", Neo4SfmConstants.groupIdProperty, endNode);
			print("  {0}", Neo4SfmConstants.artifactIdProperty, endNode);
			print("    {0}", Neo4SfmConstants.versionProperty, endNode);
			print("      {0}", Neo4SfmConstants.digestProperty, endNode);
		}

		public static void print(String pattern, String property, Node node) {
			if (node.hasProperty(property))
				System.out.println(MessageFormat.format(pattern, node.getProperty(property)));
		}

		public static String allProperties(Node node) {
			StringBuilder builder = new StringBuilder();
			for (String propertyKey : node.getPropertyKeys()) {
				if (builder.length() > 0)
					builder.append(",");
				builder.append(propertyKey = "=" + node.getProperty(propertyKey));
			}
			return builder.toString();
		}
	}

}
