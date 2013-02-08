package org.softwarefm.mavenRip.graph.internal;

import java.text.MessageFormat;
import java.util.Iterator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;
import org.softwarefm.mavenRip.graph.INeo4Sfm;
import org.softwarefm.mavenRip.graph.Neo4SfmConstants;
import org.softwarefm.mavenRip.graph.SoftwareFmRelationshipTypes;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.functions.IFunction1;

public class Neo4Sfm implements INeo4Sfm {
	private final GraphDatabaseService graphDb;
	private final Node groupReference;
	final Index<Node> groupReferenceIndex;
	private final Index<Node> fullIdIndex;
	private final Index<Node> artifactIndex;

	public Neo4Sfm(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
		groupReferenceIndex = graphDb.index().forNodes("groupReference");
		fullIdIndex = graphDb.index().forNodes("fullId");
		artifactIndex = graphDb.index().forNodes("artifact");
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

		Node group = find(groupReference, SoftwareFmRelationshipTypes.HAS_GROUP, Neo4SfmConstants.groupIdProperty, groupId);
		Node artifact = find(group, SoftwareFmRelationshipTypes.HAS_ARTIFACT, Neo4SfmConstants.artifactIdProperty, artifactId);
		Node versionNode = find(artifact, SoftwareFmRelationshipTypes.HAS_VERSION, Neo4SfmConstants.versionProperty, version);
		return versionNode;
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
			if (value.equals(secondNode.getProperty(property, null))) {
				return secondNode;
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
			tx.failure();
			throw WrappedException.wrap(e);
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
		fullIdIndex.add(versionNode, Neo4SfmConstants.fullIdProperty, fullId);
		artifactIndex.add(artifactNode, Neo4SfmConstants.groupArtifactidProperty, groupArtifactId);
		return versionNode;
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


}
