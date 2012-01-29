package org.softwareFm.collections.unrecognisedJar;

public class GroupidArtifactVersion {

	public final String groupId;
	public final String artifactId;
	public final String version;

	public GroupidArtifactVersion(String groupId, String artifactId, String version) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	@Override
	public String toString() {
		return "GroupIdArtifactVersion [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version + "]";
	}
}
