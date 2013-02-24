package org.softwarefm.mavenRip.graph;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.softwarefm.mavenRip.MavenRip;
import org.softwarefm.utilities.callbacks.ICallback;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import scala.concurrent.forkjoin.ForkJoinPool;
import scala.concurrent.forkjoin.RecursiveAction;

public class MysqlToNeo4j {

	public static final int addTaskThreshold = 2;
	public static final int addGroupThreshold = 100;
	final AtomicInteger groups = new AtomicInteger();
	final AtomicInteger artifacts = new AtomicInteger();
	final AtomicInteger digests = new AtomicInteger();
	final INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
	final JdbcTemplate template = new JdbcTemplate(MavenRip.dataSource);
	ForkJoinPool pool = new ForkJoinPool();

	public MysqlToNeo4j(int threads) {
		List<String> groupIds = template.queryForList("select distinct groupId from maven  ", String.class);
		addGroups(groupIds);
		pool.invoke(new AddTask(groupIds, 0, groupIds.size()));
	}

	class AddTask extends RecursiveAction {
		final List<String> groupIds;
		final int lo;
		final int hi;

		AddTask(List<String> groupIds, int lo, int hi) {
			this.groupIds = groupIds;
			this.lo = lo;
			this.hi = hi;
		}

		@Override
		protected void compute() {
			if (hi - lo < addTaskThreshold)
				neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
					@Override
					public void process(GraphDatabaseService t) throws Exception {
						for (int i = lo; i < hi; i++) {
							final String groupId = groupIds.get(i);
							groups.incrementAndGet();
							template.query("select distinct artifactId as artifactId from maven where groupId = ?", new RowCallbackHandler() {
								@Override
								public void processRow(ResultSet rs) throws SQLException {
									final String artifactId = rs.getString("artifactId");
									artifacts.incrementAndGet();
									try {
										System.out.println("Artifact: " + groupId + ":" + artifactId);
										template.query("select version, digest, pomUrl,pom from maven where groupId=? and artifactId=? ", new RowCallbackHandler() {
											@Override
											public void processRow(final ResultSet rsInner) throws SQLException {
												digests.incrementAndGet();
												try {
													final String digest = rsInner.getString("digest");
													if (digest != null && !digest.equals("failed")) {
														String version = rsInner.getString("version");
														String pomUrl = rsInner.getString("pomUrl");
														String pom = rsInner.getString("pom");
														Node versionNode = neo4Sfm.addGroupArtifactVersionDigest(groupId, artifactId, version, pomUrl, pom,digest);
														System.out.println(groups.get() + " " + artifacts.get() + " " + digests.get() + " " + versionNode.getProperty(Neo4SfmConstants.fullIdProperty));

													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}, groupId, artifactId);
									} catch (DataAccessException e) {
										e.printStackTrace();
									}
								}
							}, groupId);
						}
					}
				});
			else {
				int mid = (lo + hi) >>> 1;
				invokeAll(new AddTask(groupIds, lo, mid), new AddTask(groupIds, mid, hi));
			}
		}
	}

	class AddGroupTask extends RecursiveAction {
		final List<String> groupIds;
		final int lo;
		final int hi;

		AddGroupTask(List<String> groupIds, int lo, int hi) {
			this.groupIds = groupIds;
			this.lo = lo;
			this.hi = hi;
		}

		@Override
		protected void compute() {
			if (hi - lo < addGroupThreshold)
				neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
					@Override
					public void process(GraphDatabaseService t) throws Exception {
						for (int i = lo; i < hi; i++) {
							final String groupId = groupIds.get(i);
							neo4Sfm.execute(new ICallback<GraphDatabaseService>() {
								@Override
								public void process(GraphDatabaseService t) throws Exception {
									neo4Sfm.findOrCreate(neo4Sfm.graphReference(), SoftwareFmRelationshipTypes.HAS_GROUP, Neo4SfmConstants.groupIdProperty, groupId);
								}
							});
						}
						System.out.println("Added Group: " + lo + ", " + hi);
					}
				});
			else {
				int mid = (lo + hi) >>> 1;
				invokeAll(new AddGroupTask(groupIds, lo, mid), new AddGroupTask(groupIds, mid, hi));
			}
		}
	}

	public void addGroups(List<String> groupIds) {
		System.out.println("Adding raw groups");
		pool.invoke(new AddGroupTask(groupIds, 0, groupIds.size()));
		System.out.println("Added raw groups");
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new MysqlToNeo4j(4);
	}
}
