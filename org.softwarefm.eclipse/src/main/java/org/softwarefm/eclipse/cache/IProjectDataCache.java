package org.softwarefm.eclipse.cache;

import java.io.File;
import java.util.Map;

import org.softwarefm.eclipse.jdtBinding.ProjectData;
import org.softwarefm.eclipse.selection.FileAndDigest;
import org.softwarefm.utilities.exceptions.CannotAddDuplicateKeyException;
import org.softwarefm.utilities.maps.IHasCache;
import org.softwarefm.utilities.maps.Maps;

public interface IProjectDataCache extends IHasCache {

	void addNotFound(FileAndDigest fileAndDigest);

	void addProjectData(ProjectData projectData);

	CachedProjectData projectData(File file);

	public static class Utils {
		public static IProjectDataCache projectDataCache() {
			return new IProjectDataCache() {
				private final Map<File, CachedProjectData> cache = Maps.newMap();

				public void clearCaches() {
					cache.clear();
				}

				public CachedProjectData projectData(File file) {
					return cache.get(file);
				}

				public void addProjectData(ProjectData projectData) {
					File file = projectData.fileAndDigest.file;
					if (cache.containsKey(file))
						throw new CannotAddDuplicateKeyException(file.toString());
					cache.put(file, CachedProjectData.found(projectData));
				}

				public void addNotFound(FileAndDigest fileAndDigest) {
					File file = fileAndDigest.file;
					if (cache.containsKey(file))
						throw new CannotAddDuplicateKeyException(file.toString());
					cache.put(file, CachedProjectData.notFound(fileAndDigest));

				}
			};
		}
	}

}
