package org.softwareFm.server;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.internal.GitServer;

/**
 * IGitServer is a 'thread safe' (i.e. most operations are wrapped in Files.doFileLockOperation) wrapper around git facard. The local root, and the remote uri prefix are known
 * 
 * 
 * 
 * Design note: It is perhaps an unnecessary layer, but without it we are passing root around everywhere, and having to remember the File.doFileLockOperations.
 */
public interface IGitServer extends ILocalGitClient {

	void createRepository(String url);

	File findRepositoryUrl(String url);

	void clone(String url);

	void pull(String url);

	/**
	 * Map should just have simple values: strings, numbers, arrays of strings/numbers <br />
	 * This commits to the repository after the post. The operation is performed inside a file lock, so it should be thread safe
	 */
	@Override
	void post(String url, Map<String, Object> map);

	public static class Utils {

		public static void cloneOrPull(IGitServer server, String uri) {
			File existing = server.findRepositoryUrl(uri);
			if (existing == null)
				server.clone(uri);
			else
				server.pull(uri);
		}

		public static IGitServer gitServer(File root, String remoteUriPrefix) {
			return new GitServer(IGitFacard.Utils.makeFacard(), root, remoteUriPrefix);
		}

	}

}
