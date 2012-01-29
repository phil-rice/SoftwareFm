package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.internal.GitLocal;
import org.softwareFm.utilities.functions.IFunction1;

/** These are all blocking calls that may take a long time to execute */
public interface IGitLocal {
	void init(String url);

	void put(IFileDescription fileDescription, Map<String, Object> data);

	File getRoot();

	Map<String, Object> getFile(IFileDescription fileDescription);

	Map<String, Object> getFileAndDescendants(IFileDescription fileDescription);

	void clearCaches();

	public static class Utils {

		public static IGitLocal localReader(IFunction1<String, String> findRepositoryRoot, IGitOperations gitOperations, IGitWriter gitWriter, String remotePrefix, int period) {
			return new GitLocal(findRepositoryRoot, gitOperations, gitWriter, remotePrefix, period);
		}
	}
}
