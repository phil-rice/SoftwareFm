package org.softwareFm.server;

import java.io.File;
import java.util.Map;

public interface IGitServer extends ILocalGitClient {
	void createRepository(String url);

	File findRepositoryUrl(String url);

	/**
	 * Map should just have simple values: strings, numbers, arrays of strings/numbers <br />
	 * This commits to the repository after the post. The operation is performed inside a file lock, so it should be thread safe
	 */
	@Override
	void post(String url, Map<String, Object> map);

}
