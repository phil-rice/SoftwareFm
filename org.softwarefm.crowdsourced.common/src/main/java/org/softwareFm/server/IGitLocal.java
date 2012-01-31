package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.internal.GitLocal;
import org.softwareFm.utilities.maps.IHasCache;

/** These are all blocking calls that may take a long time to execute */
public interface IGitLocal extends IHasCache{
	void init(String url);

	void put(IFileDescription fileDescription, Map<String, Object> data);

	Map<String, Object> getFile(IFileDescription fileDescription);

	Map<String, Object> getFileAndDescendants(IFileDescription fileDescription);

	void delete(IFileDescription fileDescription);

	void clearCache(String url);

	File getRoot();

	public static class Utils {

		public static IGitLocal localReader(IRepoFinder repoFinder, IGitOperations gitOperations, IGitWriter gitWriter, String remotePrefix, int period) {
			return new GitLocal(repoFinder, gitOperations, gitWriter, remotePrefix, period);
		}

	}

}
