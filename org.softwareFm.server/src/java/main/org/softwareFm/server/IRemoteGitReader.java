package org.softwareFm.server;

import java.util.concurrent.Future;

import org.softwareFm.utilities.callbacks.ICallback;

public interface IRemoteGitReader {

	/** the url can be any url required by a softwarefm client. This either clones or pulls the 'appropriate' repository that the url is 'in'. The result (and the callback parameter) is the root url of the repository */ 
	Future<String> cloneOrPull(String url, ICallback<String> callback);
}
