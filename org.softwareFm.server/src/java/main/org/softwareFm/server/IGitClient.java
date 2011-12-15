package org.softwareFm.server;

import java.util.concurrent.Future;

import org.softwareFm.utilities.callbacks.ICallback;

public interface IGitClient {
	
	

	/** the url can be any url required by a softwarefm client. This either clones or pulls the 'appropriate' repository that the url is 'in'. The result (and callback parameter) is the root url of the repository */
	Future<String> cloneOrPull(String url, ICallback<String> callback);

	public static class Utils {
		public static IGitClient noClient() {
			return new IGitClient() {

				@Override
				public Future<String> cloneOrPull(String url, ICallback<String> callback) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}
}
