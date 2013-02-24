package org.softwarefm.mavenRip.graph;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.softwarefm.utilities.callbacks.ICallback;

public class Neo4JRemoveRedundantLinks {
	public static void main(String[] args) {
		final INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
		neo4Sfm.accept(new Neo4JGroupArtifactVersionDigestAdapter() {
			@Override
			public void accept(Node groupNode) {
				clean(groupNode);
			}

			@Override
			public void accept(Node groupNode, Node artifactNode) {
				clean(artifactNode);
			}

			@Override
			public void accept(Node groupNode, Node artifactNode, Node versionNode) {
				clean(versionNode);
			}

			private void clean(final Node node) {
				if (neo4Sfm.hasRedundantRelations(node))
					neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
						@Override
						public void process(GraphDatabaseService t) throws Exception {
							System.out.print("Cleaning ");
							INeo4Sfm.Utils.print(node);
							neo4Sfm.removeRedundantRelations(node);
						}
					});
			}
		});

	}
}
