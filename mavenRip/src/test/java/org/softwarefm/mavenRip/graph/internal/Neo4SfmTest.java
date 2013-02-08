package org.softwarefm.mavenRip.graph.internal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
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

	public void testFind() {
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
}
