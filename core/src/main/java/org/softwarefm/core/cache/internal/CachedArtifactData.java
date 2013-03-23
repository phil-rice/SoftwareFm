package org.softwarefm.core.cache.internal;

import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;

public class CachedArtifactData {

	public static CachedArtifactData found(ArtifactData artifactData) {
		return new CachedArtifactData(null, artifactData);
	}

	public static CachedArtifactData notFound(FileAndDigest fileAndDigest) {
		return new CachedArtifactData(fileAndDigest, null);
	}

	public final FileAndDigest fileAndDigest;
	public final ArtifactData artifactData;

	private CachedArtifactData(FileAndDigest fileAndDigest, ArtifactData artifactData) {
		this.fileAndDigest = fileAndDigest;
		this.artifactData = artifactData;
	}

	public boolean found() {
		return artifactData != null;
	}

	@Override
	public String toString() {
		return "CachedArtifactData [fileAndDigest=" + fileAndDigest + ", artifactData=" + artifactData + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileAndDigest == null) ? 0 : fileAndDigest.hashCode());
		result = prime * result + ((artifactData == null) ? 0 : artifactData.hashCode());
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
		CachedArtifactData other = (CachedArtifactData) obj;
		if (fileAndDigest == null) {
			if (other.fileAndDigest != null)
				return false;
		} else if (!fileAndDigest.equals(other.fileAndDigest))
			return false;
		if (artifactData == null) {
			if (other.artifactData != null)
				return false;
		} else if (!artifactData.equals(other.artifactData))
			return false;
		return true;
	}

}
