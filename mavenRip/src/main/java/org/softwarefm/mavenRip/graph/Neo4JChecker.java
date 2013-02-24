package org.softwarefm.mavenRip.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.softwarefm.core.maven.IMaven;
import org.softwarefm.mavenRip.MavenRip;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.functions.IFunction1;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class Neo4JChecker {
	public static void main(String[] args) {
		final INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("d:\\neo4j");
		BasicDataSource datasource = MavenRip.dataSource;
		final JdbcTemplate template = new JdbcTemplate(datasource);
		final IMaven maven = IMaven.Utils.makeImport(new File("mavenRip"));
		neo4Sfm.accept(new Neo4JGroupArtifactVersionDigestAdapter() {
			@Override
			public void accept(Node groupNode, Node artifactNode, Node versionNode) {
				List<Node> fromModel = dependantNodesFromModel(versionNode);
				List<Node> fromNeo4j = INeo4Sfm.Utils.getDependents(versionNode, Direction.OUTGOING);
				Object id = versionNode.getProperty(Neo4SfmConstants.fullIdProperty);
				checkContainsAll("FromModel for [" + id + "]. Neo4J doesnt contain ", fromModel, fromNeo4j, INeo4Sfm.Utils.node2groupIdArtifactVersionId);
				checkContainsAll("Meo4J for [" + id + "]. FromModel doesnt contain ", fromNeo4j, fromModel, INeo4Sfm.Utils.node2groupIdArtifactVersionId);
			}

			private void checkContainsAll(String prefix, List<Node> left, List<Node> right, IFunction1<Node, String> toStringFn) {
				for (Node r : right)
					checkContainsAll(prefix, left, r, toStringFn);
			}

			private void checkContainsAll(String prefix, List<Node> left, Node r, IFunction1<Node, String> toStringFn) {
				try {
					long rightId = r.getId();
					for (Node l : left) {
						if (rightId == l.getId())
							return;
					}
					System.err.println(prefix + IFunction1.Utils.apply(toStringFn, r));
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}

			private List<Node> dependantNodesFromModel(Node versionNode) {
				List<Node> result = new ArrayList<Node>();

				String pomUrl = (String) versionNode.getProperty(Neo4SfmConstants.pomUrlProperty);
				String pomText = getPomText(template, pomUrl);
				Model model = maven.pomStringToModel(pomText, "UTF-8");
				for (Dependency dependency : model.getDependencies()) {
					String groupId = dependency.getGroupId();
					String artifactId = dependency.getArtifactId();
					String version = dependency.getVersion();
					Node found = neo4Sfm.find(groupId, artifactId, version);
					if (found != null) 
						result.add(found);
				}
				return result;
			}

			private String getPomText(final JdbcTemplate template, String pomUrl) {
				try {
					String pomText = template.queryForObject("select distinct pom from maven where pomurl=?", String.class, pomUrl);
					return pomText;
				} catch (DataAccessException e) {
					System.err.println(pomUrl);
					throw WrappedException.wrap(e);
				}
			}

			@Override
			public void accept(Node groupNode, Node artifactNode) {
				System.out.println("Checking " + groupNode.getProperty(Neo4SfmConstants.groupIdProperty) + ":" + artifactNode.getProperty(Neo4SfmConstants.artifactIdProperty));
			}

			@Override
			public void accept(Node groupNode) {
			}
		});
	}

}
