package org.softwarefm.mavenRip.graph;

import java.io.File;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.utilities.callbacks.ICallback;

public class Neo4JAddProjectLinks {
	public static void main(String[] args) {
		INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
		final Node groupReference = neo4Sfm.graphReference();
		TraversalDescription description = INeo4Sfm.Utils.groupIdArtifactVersionDigest();
		final IMaven maven = IMaven.Utils.makeImport(new File("mavenRip"));

		int totalCount = 0;
		int failedCount = 0;
		for (Path path : description.traverse(groupReference)) {
			// System.out.println(path);
			totalCount++;
			final Node versionNode = path.endNode();
			if (versionNode.hasProperty(Neo4SfmConstants.pomProperty)) {
				String pom = (String) versionNode.getProperty(Neo4SfmConstants.pomProperty);
				try {
					Model model = maven.pomStringToModel(pom, "UTF-8");
					for (Dependency dependency : model.getDependencies()) {
						String groupId = dependency.getGroupId();
						String artifactId = dependency.getArtifactId();
						String version = dependency.getVersion();
						// problem want to find not find or create
						final Node targetNode = neo4Sfm.find(groupId, artifactId, version);
						if (targetNode != null) {
							neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
								@Override
								public void process(GraphDatabaseService t) throws Exception {
									INeo4Sfm.Utils.addDependency(versionNode, targetNode);
									System.out.println("Linking " + versionNode.getProperty(Neo4SfmConstants.fullIdProperty) + " to " + targetNode.getProperty(Neo4SfmConstants.fullIdProperty));
								}

							
							});
						}
					}
				} catch (Exception e) {
					System.out.print( failedCount++ +"/" + totalCount +" failed " + pom + " ");
					e.printStackTrace();
				}
			}
		}
	}
}
