package org.softwarefm.mavenRip.graph.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.softwarefm.mavenRip.graph.INeo4JGroupArtifactVersionDigestVistor;
import org.softwarefm.mavenRip.graph.INeo4Sfm;
import org.softwarefm.mavenRip.graph.Neo4SfmConstants;
import org.softwarefm.mavenRip.graph.SoftwareFmRelationshipTypes;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.tests.Tests;

public class Neo4SfmTest extends AbstractNeo4SfmTest {

	public void testGraphReferenceNodeIsCreated() {
		Node graphReference = neo4Sfm.graphReference();
		assertNotNull(graphReference);
		assertSame(graphReference, neo4Sfm.graphReference());
	}

	public void testFindOrCreateGroupReferenceFindsIfExists() {
		assertEquals(neo4Sfm.graphReference().getId(), neo4Sfm.findOrCreateGroupReference(neo4Sfm.groupReferenceIndex).getId());
		assertEquals(neo4Sfm.graphReference().getId(), neo4Sfm.findOrCreateGroupReference(neo4Sfm.groupReferenceIndex).getId());
	}

	public void testExecuteCommitsIfNoExceptionsWithCallback() {
		final AtomicInteger count = new AtomicInteger();
		final AtomicInteger initial = new AtomicInteger();
		final AtomicReference<Node> node = new AtomicReference<Node>();
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@SuppressWarnings("deprecation")
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				initial.set(Iterables.size(db.getAllNodes()));
				node.set(db.createNode());
				assertEquals(initial.get() + 1, Iterables.size(db.getAllNodes()));
				count.incrementAndGet();
			}
		});
		checkHaveNodes(initial.get() + 1);
		assertEquals(1, count.get());

	}

	public void testExecuteCommitsIfNoExceptionsWithFn() {
		final AtomicInteger count = new AtomicInteger();
		final AtomicReference<Node> node = new AtomicReference<Node>();
		final Integer initial = neo4Sfm.execute(new IFunction1<GraphDatabaseService, Integer>() {
			@SuppressWarnings("deprecation")
			@Override
			public Integer apply(GraphDatabaseService db) throws Exception {
				int result = Iterables.size(db.getAllNodes());
				node.set(db.createNode());
				assertEquals(result + 1, Iterables.size(db.getAllNodes()));
				count.incrementAndGet();
				return result;
			}
		});
		checkHaveNodes(initial + 1);
		assertEquals(1, count.get());

	}

	public void testExecuteRollbackIfExceptionsWithCallback() {
		final RuntimeException runtimeException = new RuntimeException();
		final AtomicInteger count = new AtomicInteger();
		final AtomicInteger initial = new AtomicInteger();
		final AtomicReference<Node> node = new AtomicReference<Node>();
		assertEquals(runtimeException, Tests.assertThrows(RuntimeException.class, new Runnable() {
			public void run() {
				neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
					@SuppressWarnings("deprecation")
					@Override
					public void process(GraphDatabaseService db) throws Exception {
						initial.set(Iterables.size(db.getAllNodes()));
						node.set(db.createNode());
						assertEquals(initial.get() + 1, Iterables.size(db.getAllNodes()));
						count.incrementAndGet();
						throw runtimeException;
					}
				});
			}
		}));
		checkHaveNodes(initial.get());
		assertEquals(1, count.get());

	}

	public void testExecuteRollsbackIfExceptionsWithFn() {
		final AtomicInteger count = new AtomicInteger();
		final RuntimeException runtimeException = new RuntimeException();
		final AtomicReference<Node> node = new AtomicReference<Node>();
		final AtomicInteger initial = new AtomicInteger();
		assertEquals(runtimeException, Tests.assertThrows(RuntimeException.class, new Runnable() {
			public void run() {
				neo4Sfm.execute(new IFunction1<GraphDatabaseService, Integer>() {
					@SuppressWarnings("deprecation")
					@Override
					public Integer apply(GraphDatabaseService db) throws Exception {
						initial.set(Iterables.size(db.getAllNodes()));
						node.set(db.createNode());
						assertEquals(initial.get() + 1, Iterables.size(db.getAllNodes()));
						count.incrementAndGet();
						throw runtimeException;
					}
				});

			}
		}));
		checkHaveNodes(initial.get());
		assertEquals(1, count.get());
	}

	public void testFindOrCreateSecondNodeCreatesIfDoesntExistButFindsIfDoes() {
		int initial = countNodes();
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService t) throws Exception {
				Node node1 = neo4Sfm.findOrCreate(neo4Sfm.graphReference(), SoftwareFmRelationshipTypes.HAS_GROUP, "p1", "v1");
				assertEquals(node1.getId(), neo4Sfm.findOrCreate(neo4Sfm.graphReference(), SoftwareFmRelationshipTypes.HAS_GROUP, "p1", "v1").getId());
				assertEquals(node1.getId(), neo4Sfm.findOrCreate(neo4Sfm.graphReference(), SoftwareFmRelationshipTypes.HAS_GROUP, "p1", "v1").getId());
			}
		});
		checkHaveNodes(initial + 1);
	}

	public void testFindOrCreateSecondNodeCreatesIfDoesntExistButFindsIfDoesWithTwo() {
		int initial = countNodes();
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService t) throws Exception {
				Node node1 = neo4Sfm.findOrCreate(neo4Sfm.graphReference(), SoftwareFmRelationshipTypes.HAS_GROUP, "p1", "v1");
				assertEquals(node1.getId(), neo4Sfm.findOrCreate(neo4Sfm.graphReference(), SoftwareFmRelationshipTypes.HAS_GROUP, "p1", "v1").getId());

				Node node2 = neo4Sfm.findOrCreate(neo4Sfm.graphReference(), SoftwareFmRelationshipTypes.HAS_GROUP, "p1", "v2");
				assertEquals(node2.getId(), neo4Sfm.findOrCreate(neo4Sfm.graphReference(), SoftwareFmRelationshipTypes.HAS_GROUP, "p1", "v2").getId());
				assertEquals(node2.getId(), neo4Sfm.findOrCreate(neo4Sfm.graphReference(), SoftwareFmRelationshipTypes.HAS_GROUP, "p1", "v2").getId());

				assertEquals(node1.getId(), neo4Sfm.findOrCreate(neo4Sfm.graphReference(), SoftwareFmRelationshipTypes.HAS_GROUP, "p1", "v1").getId());
			}
		});
		checkHaveNodes(initial + 2);
	}

	public void testFind3() {
		assertNull(neo4Sfm.find("g", "a", "v"));
		Node node = neo4Sfm.execute(new IFunction1<GraphDatabaseService, Node>() {
			@Override
			public Node apply(GraphDatabaseService t) throws Exception {
				return neo4Sfm.addGroupArtifactVersionDigest("g", "a", "v", "pomUrl", "pomText", "digest");
			}
		});
		assertEquals(node.getId(), neo4Sfm.find("g", "a", "v").getId());
		assertEquals(node.getId(), neo4Sfm.find("g", "a", "v").getId());
	}

	public void testFind2() {
		assertNull(neo4Sfm.find("g", "a"));
		Node versionNode = neo4Sfm.execute(new IFunction1<GraphDatabaseService, Node>() {
			@Override
			public Node apply(GraphDatabaseService t) throws Exception {
				return neo4Sfm.addGroupArtifactVersionDigest("g", "a", "v", "pomUrl", "pomText", "digest");
			}
		});
		Node node = INeo4Sfm.Utils.getArtifactFromVersion(versionNode);
		assertEquals(node.getId(), neo4Sfm.find("g", "a").getId());
		assertEquals(node.getId(), neo4Sfm.find("g", "a").getId());
	}

	public void testWalkIndexWhenZeroHitsReturnsNull() {
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				Index<Node> index = db.index().forNodes("index");
				assertNull(neo4Sfm.getNodeOrNull(index, "k", "v"));
			}
		});
	}

	public void testWalkIndexWhenZeroHitsReturnsCalculatedValue() {
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				Index<Node> index = db.index().forNodes("index");
				final Node node = db.createNode();
				index.add(node, "k", "v");
				assertEquals(node.getId(), neo4Sfm.getNodeOrNull(index, "k", "v").getId());
			}
		});
	}

	public void testaddGroupArtifactVersionDigestAddsArtifactAndVersionToIndices() {
		final AtomicReference<Node> version1 = new AtomicReference<Node>();
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				version1.set(neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "v1", "pomUrl", "pomText", "digest"));
			}
		});
		final long id1 = version1.get().getId();
		assertEquals(id1, neo4Sfm.find("g1", "a1", "v1").getId());
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				assertEquals(id1, Iterables.getOnly(db.index().forNodes(Neo4SfmConstants.fullIdIndexName).get(Neo4SfmConstants.fullIdProperty, "g1:a1:v1")).getId());
				Node artifactNode = INeo4Sfm.Utils.getArtifactFromVersion(version1.get());
				assertEquals(artifactNode.getId(), Iterables.getOnly(db.index().forNodes(Neo4SfmConstants.groupArtifactIdIndexName).get(Neo4SfmConstants.groupArtifactidProperty, "g1:a1")).getId());
			}
		});
	}

	public void testAddGroupArtifactVersionDigestIsMostlyIdempotentItRemembersLastValues() {
		final AtomicReference<Node> version1 = new AtomicReference<Node>();
		final AtomicReference<Node> version2 = new AtomicReference<Node>();
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				version1.set(neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "v1", "pomUrl", "pomText", "digest"));
			}
		});
		Node version1Node = version1.get();
		final long id1 = version1Node.getId();
		assertEquals(id1, neo4Sfm.find("g1", "a1", "v1").getId());
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				version2.set(neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "v1", "pomUrl2", "pomText2", "digest2"));
			}
		});
		assertEquals(id1, version2.get().getId());

		Node artifact1 = INeo4Sfm.Utils.getArtifactFromVersion(version1Node);

		assertEquals("pomUrl2", version1Node.getProperty(Neo4SfmConstants.pomUrlProperty));
		assertEquals("pomText2", version1Node.getProperty(Neo4SfmConstants.pomProperty));
		assertEquals("g1:a1:v1", version1Node.getProperty(Neo4SfmConstants.fullIdProperty));
		assertEquals("v1", version1Node.getProperty(Neo4SfmConstants.versionProperty));
		assertEquals("g1:a1", artifact1.getProperty(Neo4SfmConstants.groupArtifactidProperty));
	}

	public void testAddDependencyIsIdemPotent() {
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				Node n111 = neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "v1", "pomUrl", "pomText", "digest");
				Node n112 = neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "v2", "pomUrl", "pomText", "digest");
				neo4Sfm.addVersionDependency(n111, n112);
				neo4Sfm.addVersionDependency(n111, n112);
			}
		});

		Node n111 = neo4Sfm.find("g1", "a1", "v1");
		Node n112 = neo4Sfm.find("g1", "a1", "v2");
		Relationship relationship = Iterables.getOnly(n111.getRelationships(SoftwareFmRelationshipTypes.DEPENDS_ON));
		assertEquals(n111.getId(), relationship.getStartNode().getId());
		assertEquals(n112.getId(), relationship.getEndNode().getId());

	}

	public void testRemoveRedundantRelationships() {
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				Node n111 = neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "v1", "pomUrl", "pomText", "digest");
				Node n112 = neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "v2", "pomUrl", "pomText", "digest");
				neo4Sfm.addVersionDependency(n111, n112);
				assertEquals(1, Iterables.size(n111.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.DEPENDS_ON)));
				assertFalse(neo4Sfm.hasRedundantRelations(n111));
				// now add redundants
				n111.createRelationshipTo(n112, SoftwareFmRelationshipTypes.DEPENDS_ON);
				assertTrue(neo4Sfm.hasRedundantRelations(n111));
				n111.createRelationshipTo(n112, SoftwareFmRelationshipTypes.DEPENDS_ON);
				assertTrue(neo4Sfm.hasRedundantRelations(n111));
				assertEquals(3, Iterables.size(n111.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.DEPENDS_ON)));
				neo4Sfm.removeRedundantRelations(n111);
				assertFalse(neo4Sfm.hasRedundantRelations(n111));
				assertEquals(1, Iterables.size(n111.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.DEPENDS_ON)));
			}
		});
	}

	public void testWalker() {
		final List<String> log = new ArrayList<String>();
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "v1", "pomUrl", "pomText", "digest");
				neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "v2", "pomUrl", "pomText", "digest");
				neo4Sfm.addGroupArtifactVersionDigest("g1", "a1", "v2", "pomUrl", "pomText", "digest2");
				neo4Sfm.addGroupArtifactVersionDigest("g2", "a1", "v1", "pomUrl", "pomText", "digest");
			}
		});
		neo4Sfm.accept(new INeo4JGroupArtifactVersionDigestVistor() {
			@Override
			public void accept(Node groupNode, Node artifactNode, Node versionNode, Node digestNode) {
				log.add(groupName(groupNode) + ":" + artifactName(artifactNode) + ":" + versionName(versionNode) + ":" + digestName(digestNode));
			}

			@Override
			public void accept(Node groupNode, Node artifactNode, Node versionNode) {
				log.add(groupName(groupNode) + ":" + artifactName(artifactNode) + ":" + versionName(versionNode));
			}

			@Override
			public void accept(Node groupNode, Node artifactNode) {
				log.add(groupName(groupNode) + ":" + artifactName(artifactNode));
			}

			@Override
			public void accept(Node groupNode) {
				log.add(groupName(groupNode));
			}
		});
		Tests.assertListEquals(log, //
				"g1", "g1:a1", "g1:a1:v1", "g1:a1:v1:digest", "g1:a1:v2", "g1:a1:v2:digest", "g1:a1:v2:digest2",//
				"g2", "g2:a1", "g2:a1:v1", "g2:a1:v1:digest");
	}

	private String groupName(Node node) {
		return (String) node.getProperty(Neo4SfmConstants.groupIdProperty);
	}

	private String artifactName(Node node) {
		return (String) node.getProperty(Neo4SfmConstants.artifactIdProperty);
	}

	private String versionName(Node node) {
		return (String) node.getProperty(Neo4SfmConstants.versionProperty);
	}

	private String digestName(Node node) {
		return (String) node.getProperty(Neo4SfmConstants.digestProperty);
	}
}
