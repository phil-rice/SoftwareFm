package org.softwarefm.mavenRip.graph.internal;

import junit.framework.TestCase;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.softwarefm.mavenRip.graph.INeo4Sfm;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.functions.IFunction1;

abstract public class AbstractNeo4SfmTest extends TestCase {
	protected GraphDatabaseService graphDb;
	protected Neo4Sfm neo4Sfm;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		graphDb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder().newGraphDatabase();
		neo4Sfm = (Neo4Sfm) INeo4Sfm.Utils.neo4Sfm(graphDb);
	}

	@Override
	protected void tearDown() throws Exception {
		graphDb.shutdown();
		super.tearDown();
	}

	protected int countNodes() {
		int result = neo4Sfm.execute(new IFunction1<GraphDatabaseService, Integer>() {
			@SuppressWarnings("deprecation")
			@Override
			public Integer apply(GraphDatabaseService db) throws Exception {
				return Iterables.size(db.getAllNodes());
			}
		});
		return result;
	
	}

	protected void checkHaveNodes(final int n) {
		neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
			@SuppressWarnings("deprecation")
			@Override
			public void process(GraphDatabaseService db) throws Exception {
				assertEquals(n, Iterables.size(db.getAllNodes()));
			}
		});
	}
}