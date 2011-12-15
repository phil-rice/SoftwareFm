package org.softwareFm.server;

import java.util.Map;

public interface ILocalGit extends ILocalGitReader {
	/** This will delete the node at the Url */
	void delete(String url);

	/** Map should just have simple values: strings, numbers, arrays of strings/numbers */
	void post(String url, Map<String, Object> map);

	public static class Utils {
		public static GetResult getFromLocalPullIfNeeded(ILocalGitReader localGit, String url) {
			GetResult result = localGit.get(url);
			if (result.found)
				return result;
			localGit.cloneOrPull(url);
			GetResult secondResult = localGit.get(url);
			return secondResult;
		}
	}
}
