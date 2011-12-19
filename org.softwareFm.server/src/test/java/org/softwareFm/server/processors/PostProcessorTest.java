package org.softwareFm.server.processors;


import org.softwareFm.server.ServerConstants;

public class PostProcessorTest extends AbstractProcessCallTest<PostProcessor> {

	public void testIgnoresNonePosts() {
		checkIgnoresNonePosts();
	}

	public void testSendsTheMapToTheGitServer() {
		gitFacard.createRepository(remoteRoot, "a");
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(v11));
		checkContents(remoteRoot, "a/b", v11);
	}

	@Override
	protected PostProcessor makeProcessor() {
		return new PostProcessor(remoteGitServer);
	}

}
