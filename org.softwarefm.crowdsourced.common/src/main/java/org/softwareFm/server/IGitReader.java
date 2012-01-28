package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.internal.LocalReader;

public interface IGitReader {

	Map<String,Object> getFile(IFileDescription fileDescription);

	Map<String,Object> getFileAndDescendants(IFileDescription fileDescription);
	
	void clearCaches();
	

	public static class Utils {
		public static IGitReader localReader(IFindRepositoryRoot findRepositoryRoot, IGitOperations gitOperations, File root, String remotePrefix, long period) {
			return new LocalReader(findRepositoryRoot, gitOperations, root, remotePrefix, period);
		}
	}

}