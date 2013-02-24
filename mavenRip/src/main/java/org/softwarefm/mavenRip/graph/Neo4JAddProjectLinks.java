package org.softwarefm.mavenRip.graph;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.collections.Lists;

import scala.concurrent.forkjoin.ForkJoinPool;
import scala.concurrent.forkjoin.RecursiveAction;

public class Neo4JAddProjectLinks {
	public static final int addDependancyThreshold = 100;
	final INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
	final Node groupReference = neo4Sfm.graphReference();
	final TraversalDescription description = INeo4Sfm.Utils.groupIdArtifactVersionDigest();
	final IMaven maven = IMaven.Utils.makeImport(new File("mavenRip"));

	final AtomicInteger groupCount = new AtomicInteger();
	final AtomicInteger linkedArtifactCount = new AtomicInteger();
	final AtomicInteger linkedVersionCount = new AtomicInteger();
	final ForkJoinPool forkJoinPool = new ForkJoinPool(1);

	public Neo4JAddProjectLinks() {
		List<Node> groups = Lists.map(neo4Sfm.graphReference().getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_GROUP), INeo4Sfm.Utils.endNodeFn);
		System.out.println("Groups found");
		forkJoinPool.invoke(new AddDependancyTask(groups, 0, groups.size()));
	}

	class AddDependancyTask extends RecursiveAction {
		final List<Node> groupNodes;
		final int lo;
		final int hi;

		AddDependancyTask(List<Node> groupIds, int lo, int hi) {
			this.groupNodes = groupIds;
			this.lo = lo;
			this.hi = hi;
		}

		@Override
		protected void compute() {
			if (hi - lo < addDependancyThreshold)
				neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
					@Override
					public void process(GraphDatabaseService t) throws Exception {
						try {
							for (int i = lo; i < hi; i++) {
								final Node groupNode = groupNodes.get(i);
								neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
									@Override
									public void process(GraphDatabaseService t) throws Exception {
										try {
											for (Relationship groupToArtifactRelationship : groupNode.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_ARTIFACT)) {
												Node artifactNode = groupToArtifactRelationship.getEndNode();
												for (Relationship artifactToVersion : artifactNode.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_VERSION)) {
													Node versionNode = artifactToVersion.getEndNode();
													String pom = (String) versionNode.getProperty(Neo4SfmConstants.pomProperty, null);
													if (pom != null) {
														Model model = maven.pomStringToModel(pom, "UTF-8");
														for (Dependency dependency : model.getDependencies()) {
															String groupId = dependency.getGroupId();
															String artifactId = dependency.getArtifactId();
															String version = dependency.getVersion();
															Node targetArtifactNode = neo4Sfm.find(groupId, artifactId);
															if (targetArtifactNode != null) {
																neo4Sfm.addArtifactDependency(artifactNode, targetArtifactNode);
																linkedArtifactCount.incrementAndGet();
																System.out.println("Artifact " + artifactNode.getProperty(Neo4SfmConstants.artifactIdProperty) + " to " + targetArtifactNode.getProperty(Neo4SfmConstants.artifactIdProperty));
																if (version != null) {
																	// problem want to find not find or create
																	final Node targetNode = neo4Sfm.find(groupId, artifactId, version);
																	if (targetNode != null) {
																		linkedVersionCount.incrementAndGet();
																		neo4Sfm.addVersionDependency(versionNode, targetNode);
																		System.out.println("Version " + versionNode.getProperty(Neo4SfmConstants.fullIdProperty) + " to " + targetNode.getProperty(Neo4SfmConstants.fullIdProperty));
																	}
																}
															}
														}
													}
												}
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.out.println("Added Group: " + lo + ", " + hi);
					}
				});
			else {
				int mid = (lo + hi) >>> 1;
				invokeAll(new AddDependancyTask(groupNodes, lo, mid), new AddDependancyTask(groupNodes, mid, hi));
			}
		}
	}

	private void dump() {
		System.out.println("Groups: " + groupCount.get());
		System.out.println("Artifacts: " + linkedArtifactCount.get());
		System.out.println("Versions: " + linkedVersionCount.get());
	}

	public static void main(String[] args) {
		new Neo4JAddProjectLinks().dump();
	}
}
