package org.softwareFm.common;

import java.io.File;
import java.util.Map;

import org.softwareFm.common.internal.GitLocal;
import org.softwareFm.common.maps.IHasUrlCache;

/** These are all blocking calls that may take a long time to execute */
public interface IGitLocal extends IHasUrlCache {
	void init(String url);

	void put(IFileDescription fileDescription, Map<String, Object> data);

	Map<String, Object> getFile(IFileDescription fileDescription);

	String getFileAsString(IFileDescription fileDescription);

	Map<String, Object> getFileAndDescendants(IFileDescription fileDescription);

	void delete(IFileDescription fileDescription);

	@Override
	void clearCache(String url);

	File getRoot();

	abstract public static class Utils {

		public static IGitLocal localReader(IRepoFinder repoFinder, IGitOperations gitOperations, IGitWriter gitWriter, String remotePrefix, int period) {
			return new GitLocal(repoFinder, gitOperations, gitWriter, remotePrefix, period);
		}

	}

}
