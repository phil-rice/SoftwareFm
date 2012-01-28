package org.softwareFm.server.processors.internal;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.maps.Maps;

public class PostProcessorTest extends AbstractProcessCallTest<PostProcessor> {

	public void testIgnoresNonePosts() {
		checkIgnoresNonePosts();
	}

	public void testSendsTheMapToTheGitServer() {
		remoteOperations.init("a");
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(v11));
		checkContents(remoteRoot, "a/b", v11);
	}

	@SuppressWarnings("unchecked")
	public void testMergesTheNewDataWithOldData() {
		remoteOperations.init("a");
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(v11));
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(a1b2));
		checkContents(remoteRoot, "a/b", Maps.<String, Object> merge(v11, a1b2));
	}

	public void testNewDataReplacesOld() {
		remoteOperations.init("a");
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(v11));
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(Maps.stringObjectMap("v", 2)));
		checkContents(remoteRoot, "a/b", v12);
	}

	public void testDontCopySubDirectoriesIntoFile() {
		remoteOperations.init("a");
		processor.process(makeRequestLine(ServerConstants.POST, "a/b/c"), makeDataMap(a1b2));
		processor.process(makeRequestLine(ServerConstants.POST, "a/b/c/d"), makeDataMap(v22));

		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(v11));
		processor.process(makeRequestLine(ServerConstants.POST, "a/b"), makeDataMap(Maps.stringObjectMap("v", 2)));

		checkContents(remoteRoot, "a/b", v12);

	}

	@Override
	protected PostProcessor makeProcessor() {
		return new PostProcessor(remoteGitServer);
	}

}
