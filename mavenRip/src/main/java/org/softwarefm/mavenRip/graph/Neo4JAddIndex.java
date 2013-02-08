package org.softwarefm.mavenRip.graph;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.softwarefm.utilities.callbacks.ICallback;

public class Neo4JAddIndex {
	public static void main(String[] args) {
		final String property = Neo4SfmConstants.fullIdProperty;

		INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
		final Node groupReference = neo4Sfm.graphReference();
		TraversalDescription description = INeo4Sfm.Utils.groupIdArtifactVersionDigest();

		for (Path path : description.traverse(groupReference)) {
			final Node endNode = path.endNode();
			if (endNode.hasProperty(property))
				neo4Sfm.execute(new ICallback<GraphDatabaseService>(){
					@Override
					public void process(GraphDatabaseService t) throws Exception {
						Object value = endNode.getProperty(property);
						t.index().forNodes(Neo4SfmConstants.fullIdIndexName).add(endNode, property, value);
						System.out.println(value);
					}});
		}
	}


}
