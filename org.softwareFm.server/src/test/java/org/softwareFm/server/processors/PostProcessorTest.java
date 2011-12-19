package org.softwareFm.server.processors;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.maps.Maps;

public class PostProcessorTest extends AbstractProcessCallTest<PostProcessor> {

	public void testIgnoresNonePosts() {
		checkIgnoresNonePosts();
	}

	public void testSendsTheMapToTheGitServer() {
		gitFacard.createRepository(remoteRoot, "a");
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(v11));
		checkContents(remoteRoot, "a/b", v11);
	}

	@SuppressWarnings("unchecked")
	public void testMergesTheNewDataWithOldData() {
		gitFacard.createRepository(remoteRoot, "a");
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(v11));
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(a1b2));
		checkContents(remoteRoot, "a/b", Maps.<String, Object> merge(v11, a1b2));
	}

	public void testNewDataReplacesOld() {
		gitFacard.createRepository(remoteRoot, "a");
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(v11));
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(Maps.stringObjectMap("v", 2)));
		checkContents(remoteRoot, "a/b", v12);
	}

	@Override
	protected PostProcessor makeProcessor() {
		return new PostProcessor(remoteGitServer);
	}

}
