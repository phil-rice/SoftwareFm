package org.softwarefm.mavenRip.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.strings.Strings;

public class Neo4JToDependsOn {
	public static void main(String[] args) {

		INeo4Sfm neo4Sfm = INeo4Sfm.Utils.neo4Sfm("neo4j");
		final Node groupReference = neo4Sfm.graphReference();
		String root = "depends";
		int groups = 0;
		int artifacts = 0;
		int files = 0;
		for (Relationship hasGroup : groupReference.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_GROUP)) {
			Node groupNode = hasGroup.getEndNode();
			String groupId = (String) groupNode.getProperty(Neo4SfmConstants.groupIdProperty);
			String groupPrefix = Strings.url(root, groupId);
			groups++;
			for (Relationship hasArtifact : groupNode.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.HAS_ARTIFACT)) {
				Node artifactNode = hasArtifact.getEndNode();
				String artifactId = (String) artifactNode.getProperty(Neo4SfmConstants.artifactIdProperty);
				int i = 0;
				artifacts++;
				List<String> dependants = new ArrayList<String>();
				for (Relationship dependsOn : artifactNode.getRelationships(Direction.OUTGOING, SoftwareFmRelationshipTypes.DEPENDS_ON)) {
					Node dependsOnArtifactNode = dependsOn.getEndNode();
					Node dependsOnGroupNode = INeo4Sfm.Utils.getGroupFromArtifact(dependsOnArtifactNode);
					String dependsOnGroupId = (String) dependsOnGroupNode.getProperty(Neo4SfmConstants.groupIdProperty);
					String dependsOnArtifactId = (String) dependsOnArtifactNode.getProperty(Neo4SfmConstants.artifactIdProperty);
					dependants.add("| a" + i++ + "=" + dependsOnGroupId + "/" + dependsOnArtifactId + "\n");
				}
				if (i > 0) {
					File directory = new File(Strings.url(groupPrefix, artifactId).replace(".", File.separator));
					String header = "{{DependsOnLoop\n|count=" + i + "\n";
					String allDependantsText = header + Strings.join(dependants, "") + "}}";
					String summaryDependantsText = header + Strings.join(Lists.take(dependants, 5), "") + "}}";
					Files.setText(new File(directory, "depends"), allDependantsText);
					Files.setText(new File(directory, "dependsSummary"), summaryDependantsText);
					files++;
					System.out.println("File: " + directory);
				}
			}

		}
		System.out.println("Groups: " + groups);
		System.out.println("Artifacts: " + artifacts);
		System.out.println("Files: " + files);
	}

}
