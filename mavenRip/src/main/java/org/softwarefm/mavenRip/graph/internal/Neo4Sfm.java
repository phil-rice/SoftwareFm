package org.softwarefm.mavenRip.graph.internal;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.softwarefm.mavenRip.graph.INeo4JGroupArtifactVersionDigestVistor;
import org.softwarefm.mavenRip.graph.INeo4Sfm;
import org.softwarefm.mavenRip.graph.Neo4SfmConstants;
import org.softwarefm.mavenRip.graph.SoftwareFmRelationshipTypes;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.maps.Maps;

public class Neo4Sfm implements INeo4Sfm {
	private final GraphDatabaseService graphDb;
	private final Node groupReference;
	final Index<Node> groupReferenceIndex;
	private final Index<Node> fullIdIndex;
	private final Index<Node> artifactIndex;

	public Neo4Sfm(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
		groupReferenceIndex = graphDb.index().forNodes("groupReference");
		fullIdIndex = graphDb.index().forNodes(Neo4SfmConstants.fullIdIndexName);
		artifactIndex = graphDb.index().forNodes(Neo4SfmConstants.groupArtifactIdIndexName);
		groupReference = findOrCreateGroupReference(groupReferenceIndex);
	}

	Node findOrCreateGroupReference(final Index<Node> groupReferenceIndex) {
		Node result = groupReferenceIndex.get("root", "true").getSingle();
		if (result == null)
			result = execute(new IFunction1<GraphDatabaseService, Node>() {
				@Override
				public Node apply(GraphDatabaseService db) throws Exception {
					Node result = db.createNode();
					result.setProperty("root", "true");
					groupReferenceIndex.add(result, "root", "true");
					return result;
				}
			});
		return result;
	}

	public Node findOrCreate(Node firstNode, SoftwareFmRelationshipTypes relationShip, String property, String value) {
		Node secondNode = find(firstNode, relationShip, property, value);
		if (secondNode != null)
			return secondNode;

		secondNode = firstNode.getGraphDatabase().createNode();
		secondNode.setProperty(property, value);
		firstNode.createRelationshipTo(secondNode, relationShip);
		return secondNode;
	}

	@Override
	public Node find(String groupId, String artifactId, String version) {
		Node artifact = find(groupId, artifactId);
		Node versionNode = find(artifact, SoftwareFmRelationshipTypes.HAS_VERSION, Neo4SfmConstants.versionProperty, version);
		return versionNode;
	}

	@Override
	public Node find(String groupId, String artifactId) {
		Node group = find(groupReference, SoftwareFmRelationshipTypes.HAS_GROUP, Neo4SfmConstants.groupIdProperty, groupId);
		Node artifact = find(group, SoftwareFmRelationshipTypes.HAS_ARTIFACT, Neo4SfmConstants.artifactIdProperty, artifactId);
		return artifact;
	}

	private Node find(Node firstNode, SoftwareFmRelationshipTypes relationShip, String property, String value) {
		if (firstNode == null)
			return null;
		TraversalDescription description = Traversal.description().//
				breadthFirst().//
				relationships(relationShip).//
				uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);
		Traverser traverse = description.traverse(firstNode);
		for (Node secondNode : traverse.nodes()) {
			try {
				Object actual = secondNode.getProperty(property, null);
				if (value == actual || value.equals(actual)) {
					return secondNode;
				}
			} catch (Exception e) {
				throw new RuntimeException("Error accessing node: " + secondNode + "(" + INeo4Sfm.Utils.allProperties(secondNode) + ")", e);
			}
		}
		return null;
	}

	@Override
	public Node graphReference() {
		return groupReference;
	}

	@Override
	public <T> T execute(IFunction1<GraphDatabaseService, T> fn) {
		Transaction tx = graphDb.beginTx();
		try {
			T result = fn.apply(graphDb);
			tx.success();
			return result;
		} catch (Exception e) {
			tx.failure();
			throw WrappedException.wrap(e);
		} finally {
			tx.finish();
		}

	}

	@Override
	public void execute(ICallback<GraphDatabaseService> callback) {
		Transaction tx = graphDb.beginTx();
		try {
			callback.process(graphDb);
			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
			tx.failure();
		} finally {
			tx.finish();
		}
	}

	@Override
	public Node addGroupArtifactVersionDigest(String groupId, String artifactId, String version, String pomUrl, String pomText, String digest) {
		String groupArtifactId = INeo4Sfm.Utils.makeGroupArtifactId(groupId, artifactId);
		String fullId = INeo4Sfm.Utils.makeFullId(groupId, artifactId, version);

		Node groupNode = findOrCreate(groupReference, SoftwareFmRelationshipTypes.HAS_GROUP, Neo4SfmConstants.groupIdProperty, groupId);
		Node artifactNode = findOrCreate(groupNode, SoftwareFmRelationshipTypes.HAS_ARTIFACT, Neo4SfmConstants.artifactIdProperty, artifactId);
		Node versionNode = findOrCreate(artifactNode, SoftwareFmRelationshipTypes.HAS_VERSION, Neo4SfmConstants.versionProperty, version);

		findOrCreate(versionNode, SoftwareFmRelationshipTypes.HAS_DIGEST, Neo4SfmConstants.digestProperty, digest);
		versionNode.setProperty(Neo4SfmConstants.pomUrlProperty, pomUrl);
		versionNode.setProperty(Neo4SfmConstants.pomProperty, pomText);
		versionNode.setProperty(Neo4SfmConstants.fullIdProperty, fullId);
		artifactNode.setProperty(Neo4SfmConstants.groupArtifactidProperty, groupArtifactId);
		fullIdIndex.add(versionNode, Neo4SfmConstants.fullIdProperty, fullId);
		artifactIndex.add(artifactNode, Neo4SfmConstants.groupArtifactidProperty, groupArtifactId);
		return versionNode;
	}

	@Override
	public void addArtifactDependency(Node parentArtifactNode, Node dependencyArtifactNode) {
		createRelationshipifDoesntExist(parentArtifactNode, SoftwareFmRelationshipTypes.DEPENDS_ON, dependencyArtifactNode);

	}

	@Override
	public void addVersionDependency(Node parentVersionNode, Node dependencyVersionNode) {
		createRelationshipifDoesntExist(parentVersionNode, SoftwareFmRelationshipTypes.DEPENDS_ON, dependencyVersionNode);
	}

	private void createRelationshipifDoesntExist(Node from, RelationshipType relationshipType, Node dependent) {
		for (Relationship relation : from.getRelationships(Direction.OUTGOING, relationshipType)) {
			if (relation.getEndNode().getId() == dependent.getId())
				return;
		}
		from.createRelationshipTo(dependent, relationshipType);
	}

	@Override
	public Node getNodeOrNull(Index<Node> index, String property, Object value) {
		IndexHits<Node> indexHits = index.get(property, value);
		Iterator<Node> iterator = indexHits.iterator();
		try {
			if (iterator.hasNext()) {
				Node node = iterator.next();
				if (iterator.hasNext())
					throw new IllegalStateException(MessageFormat.format("Index for {0}={1} should have zero or one entries, has mode", property, value));
				return node;
			} else {
				return null;
			}
		} finally {
			indexHits.close();
		}
	}

	@Override
	public void removeRedundantRelations(Node from) {
		Map<RelationshipType, Map<Node, List<Relationship>>> outputs = getRelationshipsMap(from);
		for (Entry<RelationshipType, Map<Node, List<Relationship>>> typeNodeEntry : outputs.entrySet()) {
			for (Entry<Node, List<Relationship>> nodeListEntry : typeNodeEntry.getValue().entrySet()) {
				List<Relationship> relationShips = nodeListEntry.getValue();
				for (int i = 1; i < relationShips.size(); i++)
					relationShips.get(i).delete();
			}
		}
	}

	@Override
	public boolean hasRedundantRelations(Node from) {
		Map<RelationshipType, Map<Node, List<Relationship>>> outputs = getRelationshipsMap(from);
		for (Entry<RelationshipType, Map<Node, List<Relationship>>> typeNodeEntry : outputs.entrySet()) {
			for (Entry<Node, List<Relationship>> nodeListEntry : typeNodeEntry.getValue().entrySet()) {
				List<Relationship> relationShips = nodeListEntry.getValue();
				if (relationShips.size() > 1)
					return true;
			}
		}
		return false;
	}

	private Map<RelationshipType, Map<Node, List<Relationship>>> getRelationshipsMap(Node from) {
		Map<RelationshipType, Map<Node, List<Relationship>>> outputs = Maps.newMap();
		for (Relationship r : from.getRelationships())
			Maps.addToList(outputs, r.getType(), r.getEndNode(), r);
		return outputs;
	}

	@Override
	public void accept(INeo4JGroupArtifactVersionDigestVistor visitor) {
		for (Relationship r1 : groupReference.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_GROUP)) {
			Node groupNode = r1.getEndNode();
			visitor.accept(groupNode);
			for (Relationship r2 : groupNode.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_ARTIFACT)) {
				Node artifactNode = r2.getEndNode();
				visitor.accept(groupNode, artifactNode);
				for (Relationship r3 : artifactNode.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_VERSION)) {
					Node versionNode = r3.getEndNode();
					visitor.accept(groupNode, artifactNode, versionNode);
					for (Relationship r4 : versionNode.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_DIGEST)) {
						Node digestNode = r4.getEndNode();
						visitor.accept(groupNode, artifactNode, versionNode, digestNode);
					}
				}
			}
		}
	}

}
