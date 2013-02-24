package org.softwarefm.mavenRip.graph;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.tests.Tests;

public class CopyOfNeo4JAddProjectLinks {
	public static void main(String[] args) {
		final INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
		final Node groupReference = neo4Sfm.graphReference();
		final TraversalDescription description = INeo4Sfm.Utils.groupIdArtifactVersionDigest();
		final IMaven maven = IMaven.Utils.makeImport(new File("mavenRip"));

		final AtomicInteger totalCount = new AtomicInteger();
		final AtomicInteger linkedCount = new AtomicInteger();
		final AtomicInteger failedCount = new AtomicInteger();
		Tests.executeInMultipleThreads(2, new ICallback<Integer>() {
			@Override
			public void process(Integer t) throws Exception {
				for (Path path : description.traverse(groupReference)) {
					// System.out.println(path);
					totalCount.incrementAndGet();
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
											linkedCount.incrementAndGet();
											neo4Sfm.addVersionDependency(versionNode, targetNode);
											System.out.println("Linking " + versionNode.getProperty(Neo4SfmConstants.fullIdProperty) + " to " + targetNode.getProperty(Neo4SfmConstants.fullIdProperty));
										}
									});
								}
							}
						} catch (Exception e) {
							System.out.print(failedCount.incrementAndGet() + "/" + totalCount + " failed " + pom + " ");
							e.printStackTrace();
						}
					}
				}
			}
		});
		System.out.println("Failed: "+ failedCount.get());
		System.out.println("Linked: "+ linkedCount.get());
		System.out.println("Total: "+ totalCount.get());
	}
}
