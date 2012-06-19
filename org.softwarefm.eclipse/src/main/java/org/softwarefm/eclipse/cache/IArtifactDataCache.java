package org.softwarefm.eclipse.cache;

import java.io.File;
import java.util.Map;

import org.softwarefm.eclipse.jdtBinding.ArtifactData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.utilities.exceptions.CannotAddDuplicateKeyException;
import org.softwarefm.utilities.maps.IHasCache;
import org.softwarefm.utilities.maps.Maps;

public interface IArtifactDataCache extends IHasCache {

	void addNotFound(FileAndDigest fileAndDigest);

	void addProjectData(ArtifactData artifactData);

	CachedArtifactData projectData(File file);

	public static class Utils {
		public static IArtifactDataCache artifactDataCache() {
			return new IArtifactDataCache() {
				private final Map<File, CachedArtifactData> cache = Maps.newMap();

				public void clearCaches() {
					cache.clear();
				}

				public CachedArtifactData projectData(File file) {
					return cache.get(file);
				}

				public void addProjectData(ArtifactData artifactData) {
					File file = artifactData.fileAndDigest.file;
					if (cache.containsKey(file))
						throw new CannotAddDuplicateKeyException(file.toString());
					cache.put(file, CachedArtifactData.found(artifactData));
				}

				public void addNotFound(FileAndDigest fileAndDigest) {
					File file = fileAndDigest.file;
					if (cache.containsKey(file))
						throw new CannotAddDuplicateKeyException(file.toString());
					cache.put(file, CachedArtifactData.notFound(fileAndDigest));

				}
			};
		}
	}

}
