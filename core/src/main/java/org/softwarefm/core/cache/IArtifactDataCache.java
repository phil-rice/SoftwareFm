package org.softwarefm.core.cache;

import java.io.File;
import java.util.Map;

import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.selection.FileAndDigest;
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
				private final Object lock = new Object();

				public void clearCaches() {
					synchronized (lock) {
						cache.clear();
					}
				}

				public CachedArtifactData projectData(File file) {
					synchronized (lock) {
						return cache.get(file);
					}
				}

				public void addProjectData(ArtifactData artifactData) {
					synchronized (lock) {
						File file = artifactData.fileAndDigest.file;
						if (cache.containsKey(file))
							throw new CannotAddDuplicateKeyException(file.toString());
						cache.put(file, CachedArtifactData.found(artifactData));

					}
				}

				public void addNotFound(FileAndDigest fileAndDigest) {
					synchronized (lock) {
						File file = fileAndDigest.file;
						if (cache.containsKey(file))
							throw new CannotAddDuplicateKeyException(file.toString());
						cache.put(file, CachedArtifactData.notFound(fileAndDigest));
					}

				}
			};
		}

		public static IArtifactDataCache noCache() {
			return new IArtifactDataCache() {

				public void clearCaches() {
				}

				public CachedArtifactData projectData(File file) {
					return null;
				}

				public void addProjectData(ArtifactData artifactData) {
				}

				public void addNotFound(FileAndDigest fileAndDigest) {
				}
			};
		}
	}

}
