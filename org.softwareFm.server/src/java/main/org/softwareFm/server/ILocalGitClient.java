package org.softwareFm.server;

import java.util.Map;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;

public interface ILocalGitClient extends ILocalGitClientReader {
	/** This will delete the node at the Url */
	void delete(String url);

	/** Map should just have simple values: strings, numbers, arrays of strings/numbers */
	void post(String url, Map<String, Object> map);

	public static class Utils {
		public static GetResult getFromLocalPullIfNeeded(ILocalGitClientReader localGit, IGitClient gitClient, String url) {
			try {
				GetResult result = localGit.get(url);
				if (result.found)
					return result;
				gitClient.cloneOrPull(url, ICallback.Utils.<String>noCallback()).get();
				GetResult secondResult = localGit.get(url);
				return secondResult;
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}
	}
}
