package org.softwareFm.server.processors.internal;

import org.softwareFm.server.ServerConstants;

public class FindRootProcessorTest extends AbstractProcessCallTest<FindRootProcessor> {

	public void testIgnoresNoneGets() {
		checkIgnoresNoneGet();
	}

	public void testReturnsUrlOfGitRepository() {
		remoteGitServer.createRepository("a/b");
		checkGetStringFromProcessor("/"+ServerConstants.findRepositoryBasePrefix +"/a/b", "a/b");
		checkGetStringFromProcessor("/"+ServerConstants.findRepositoryBasePrefix +"/a/b/c", "a/b");
		checkGetStringFromProcessor("/"+ServerConstants.findRepositoryBasePrefix +"/a/b/c/d", "a/b");
	}

	@Override
	protected FindRootProcessor makeProcessor() {
		return new FindRootProcessor(remoteGitServer);
	}

}
