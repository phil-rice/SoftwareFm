package org.softwarefm.mavenRip.graph;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.strings.Strings;

public class Neo4JToDependsOn {

	String root = "depends";
	int groups = 0;
	int artifacts = 0;
	int files = 0;

	public Neo4JToDependsOn() {
		INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
		final Node groupReference = neo4Sfm.graphReference();
		for (Relationship hasGroup : groupReference.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_GROUP)) {
			Node groupNode = hasGroup.getEndNode();
			String groupId = (String) groupNode.getProperty(Neo4SfmConstants.groupIdProperty);
			String groupPrefix = Strings.url(root, groupId);
			groups++;
			for (Relationship hasArtifact : groupNode.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_ARTIFACT)) {
				Node artifactNode = hasArtifact.getEndNode();
				String artifactId = (String) artifactNode.getProperty(Neo4SfmConstants.artifactIdProperty);
				artifacts++;
				List<String> dependants = getDependents(artifactNode, Direction.OUTGOING);
				List<String> dependsOnThis = getDependents(artifactNode, Direction.INCOMING);
				dumpResults("depends", "dependsSummary", groupPrefix, artifactId, dependants, "DependsOnLoop");
				dumpResults("dependsOnThis", "dependsOnThisSummary", groupPrefix, artifactId, dependsOnThis, "DependsOnThisLoop");
			}

		}
	}

	private void report() {
		System.out.println("Groups: " + groups);
		System.out.println("Artifacts: " + artifacts);
		System.out.println("Files: " + files);
	}

	private void dumpResults(String fileName, String summaryFile, String groupPrefix, String artifactId, List<String> dependants, String template) {
		if (dependants.size() > 0) {
			File directory = new File(Strings.url(groupPrefix, artifactId).replace(".", File.separator));
			directory.mkdirs();
			String header = "{{" + template + "\n";
			String allDependantsText = header + Strings.join(dependants, "") + "}}";
			String summaryDependantsText = header + Strings.join(Lists.take(dependants, 5), "") + "}}";
			Files.setText(new File(directory, fileName), allDependantsText);
			Files.setText(new File(directory, summaryFile), summaryDependantsText);
			files++;
			System.out.println("File: " + directory + File.separator + fileName);
		}
	}

	private static List<String> getDependents(Node artifactNode, Direction direction) {
		final AtomicInteger i = new AtomicInteger();
		List<Node> dependents = INeo4Sfm.Utils.getDependents(artifactNode, direction);
		return Lists.map(dependents, new IFunction1<Node, String>(){
			@Override
			public String apply(Node dependsOnArtifactNode) throws Exception {
				Node dependsOnGroupNode = INeo4Sfm.Utils.getGroupFromArtifact(dependsOnArtifactNode);
				String dependsOnGroupId = (String) dependsOnGroupNode.getProperty(Neo4SfmConstants.groupIdProperty);
				String dependsOnArtifactId = (String) dependsOnArtifactNode.getProperty(Neo4SfmConstants.artifactIdProperty);
				String result = "| a" + i.getAndIncrement() + "=" + dependsOnGroupId + "/" + dependsOnArtifactId + "\n";
				return result;
			}});
	}

	public static void main(String[] args) {
		new Neo4JToDependsOn().report();
	}

}
