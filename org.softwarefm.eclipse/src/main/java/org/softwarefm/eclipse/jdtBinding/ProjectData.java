package org.softwarefm.eclipse.jdtBinding;

import org.softwarefm.eclipse.selection.FileNameAndDigest;

public class ProjectData {
	public final FileNameAndDigest fileNameAndDigest;
	public final String groupId;
	public final String artifactId;
	public final String version;

	public ProjectData(FileNameAndDigest fileNameAndDigest, String groupId, String artifactId, String version) {
		this.fileNameAndDigest = fileNameAndDigest;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	@Override
	public String toString() {
		return "ProjectData [fileNameAndDigest=" + fileNameAndDigest + ", groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((fileNameAndDigest == null) ? 0 : fileNameAndDigest.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectData other = (ProjectData) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (fileNameAndDigest == null) {
			if (other.fileNameAndDigest != null)
				return false;
		} else if (!fileNameAndDigest.equals(other.fileNameAndDigest))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
}
