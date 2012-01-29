package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.internal.LocalReader;
import org.softwareFm.utilities.functions.IFunction1;

public interface IGitReader {

	Map<String,Object> getFile(IFileDescription fileDescription);

	Map<String,Object> getFileAndDescendants(IFileDescription fileDescription);
	
	void clearCaches();
	
	File getRoot();

	public static class Utils {
		public static IGitReader localReader(IFunction1<String, String> findRepositoryRoot, IGitOperations gitOperations, File root, String remotePrefix, long period) {
			return new LocalReader(findRepositoryRoot, gitOperations, remotePrefix, period);
		}
	}

}