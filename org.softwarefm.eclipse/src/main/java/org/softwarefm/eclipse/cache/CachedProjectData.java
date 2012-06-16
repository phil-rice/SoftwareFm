package org.softwarefm.eclipse.cache;

import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileAndDigest;

public class CachedProjectData {

	public static CachedProjectData found(ProjectData projectData) {
		return new CachedProjectData(null, projectData);
	}

	public static CachedProjectData notFound(FileAndDigest fileAndDigest) {
		return new CachedProjectData(fileAndDigest, null);
	}

	public final FileAndDigest fileAndDigest;
	public final ProjectData projectData;

	private CachedProjectData(FileAndDigest fileAndDigest, ProjectData projectData) {
		this.fileAndDigest = fileAndDigest;
		this.projectData = projectData;
	}

	public boolean found() {
		return projectData != null;
	}

	@Override
	public String toString() {
		return "CachedProjectData [fileAndDigest=" + fileAndDigest + ", projectData=" + projectData + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileAndDigest == null) ? 0 : fileAndDigest.hashCode());
		result = prime * result + ((projectData == null) ? 0 : projectData.hashCode());
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
		CachedProjectData other = (CachedProjectData) obj;
		if (fileAndDigest == null) {
			if (other.fileAndDigest != null)
				return false;
		} else if (!fileAndDigest.equals(other.fileAndDigest))
			return false;
		if (projectData == null) {
			if (other.projectData != null)
				return false;
		} else if (!projectData.equals(other.projectData))
			return false;
		return true;
	}

}
