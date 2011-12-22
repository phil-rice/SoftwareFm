package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Collections;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.internal.MakeRootProcessor;

public class MakeRootProcessorTest extends AbstractProcessCallTest<MakeRootProcessor> {

	public void testIgnoresNoneGets() {
		checkIgnoresNonePosts();
	}

	public void testReturnsUrlOfGitRepository() {
		checkProcessMakesRepo("a/b/c");
		checkProcessMakesRepo("a/b/d");
		checkProcessMakesRepo("b/d");
		checkProcessMakesRepo("c");

	}

	private void checkProcessMakesRepo(String uri) {
		File file = new File(remoteRoot, uri);
		checkRepositoryDoesntExists(file);
		processor.process(makeRequestLine(ServerConstants.POST, "/" + ServerConstants.makeRootPrefix + "/"+ uri), makeDataMap(Collections.<String, Object> emptyMap()));
		checkRepositoryExists(file);
	}

	@Override
	protected MakeRootProcessor makeProcessor() {
		return new MakeRootProcessor(remoteGitServer);
	}

}
