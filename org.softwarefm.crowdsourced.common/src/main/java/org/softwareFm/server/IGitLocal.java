package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.internal.FindRepositoryForTests;
import org.softwareFm.server.internal.GitLocal;
import org.softwareFm.utilities.functions.IFunction1;

/** These are all blocking calls that may take a long time to execute */
public interface IGitLocal {
	void init(String url);

	void put(IFileDescription fileDescription, Map<String, Object> data);

	Map<String, Object> getFile(IFileDescription fileDescription);

	Map<String, Object> getFileAndDescendants(IFileDescription fileDescription);

	void delete(IFileDescription fileDescription);

	void clearCache(String url);

	void clearCaches();

	File getRoot();

	public static class Utils {

		public static IGitLocal localReader(IFunction1<String, String> findRepositoryRoot, IGitOperations gitOperations, IGitWriter gitWriter, String remotePrefix, int period) {
			return new GitLocal(findRepositoryRoot, gitOperations, gitWriter, remotePrefix, period);
		}
		
		public static IFunction1<String, String> findRepostoryForTests(File remoteRoot){
			return new FindRepositoryForTests(remoteRoot);
		}
		
	}

}
