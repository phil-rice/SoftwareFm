package org.softwareFm.server.processors.internal;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.AbstractProcessCallTest;

public class FindRootProcessorTest extends AbstractProcessCallTest<FindRootProcessor> {

	public void testIgnoresNoneGets() {
		checkIgnoresNoneGet();
	}

	public void testReturnsUrlOfGitRepository() {
		remoteOperations.init("a/b");
		checkGetStringFromProcessor("/" + CommonConstants.findRepositoryBasePrefix + "/a/b", "a/b");
		checkGetStringFromProcessor("/" + CommonConstants.findRepositoryBasePrefix + "/a/b/c", "a/b");
		checkGetStringFromProcessor("/" + CommonConstants.findRepositoryBasePrefix + "/a/b/c/d", "a/b");
	}

	@Override
	protected FindRootProcessor makeProcessor() {
		return new FindRootProcessor(remoteOperations);
	}

}
