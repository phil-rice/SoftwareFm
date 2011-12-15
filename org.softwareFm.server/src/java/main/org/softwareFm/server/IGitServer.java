package org.softwareFm.server;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.softwareFm.utilities.exceptions.WrappedException;

public interface IGitServer extends ILocalGitClient {
	void createRepository(String url);

	File findRepositoryUrl(String url);

	/** Map should just have simple values: strings, numbers, arrays of strings/numbers <br /> This commits to the repository after the post. The operation is performed inside a file lock, so it should be thread safe*/
	@Override
	void post(String url, Map<String, Object> map);

	public static class Utils {
		public static FileRepository makeFileRepository(File root, String url) {
			try {
				File dir = new File(root, url);
				FileRepositoryBuilder builder = new FileRepositoryBuilder();
				return builder.setGitDir(new File(dir, ServerConstants.gitDirectory)).readEnvironment() // scan environment GIT_* variables
						.addCeilingDirectory(root).findGitDir().build(); // scan up the file system tree
			} catch (IOException e) {
				throw WrappedException.wrap(e);
			}
		}
	}

}
