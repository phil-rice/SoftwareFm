package org.softwareFm.server;

public interface ILocalGitReader {
	GetResult get(String url);

	/** the url can be any url required by a softwarefm client. This either clones or pulls the 'appropriate' repository that the url is 'in'. The result  is the root url of the repository */
	String cloneOrPull(String url);
}
