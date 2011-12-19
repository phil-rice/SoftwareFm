package org.softwareFm.server.processors;

import java.util.Collections;

import org.softwareFm.server.ServerConstants;

public class FindRootProcessorTest extends AbstractProcessCallTest<FindRootProcessor> {

	public void testIgnoresNoneGets() {
		checkIgnoresNoneGet();
	}

	public void testReturnsUrlOfGitRepository() {
		remoteGitServer.createRepository("a/b");
		assertEquals("a/b", processor.process(makeRequestLine(ServerConstants.GET, "/"+ServerConstants.findRepositoryBasePrefix +"/a/b"), Collections.<String, Object> emptyMap()));
		assertEquals("a/b", processor.process(makeRequestLine(ServerConstants.GET, "/"+ServerConstants.findRepositoryBasePrefix +"/a/b/c"), Collections.<String, Object> emptyMap()));
		assertEquals("a/b", processor.process(makeRequestLine(ServerConstants.GET, "/"+ServerConstants.findRepositoryBasePrefix +"/a/b/c/d"), Collections.<String, Object> emptyMap()));
	}

	@Override
	protected FindRootProcessor makeProcessor() {
		return new FindRootProcessor(remoteGitServer);
	}

}
