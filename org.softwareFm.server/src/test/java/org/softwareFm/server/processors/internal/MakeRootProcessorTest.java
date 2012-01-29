package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Collections;

import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.utilities.maps.UrlCache;

public class MakeRootProcessorTest extends AbstractProcessCallTest<MakeRootProcessor> {

	public void testIgnoresNoneGets() {
		checkIgnoresNonePosts();
	}

	public void testMakesRepository() {
		checkProcessMakesRepo("a/b/c");
		checkProcessMakesRepo("a/b/d");
		checkProcessMakesRepo("b/d");
		checkProcessMakesRepo("c");
	}

	private void checkProcessMakesRepo(String uri) {
		File file = new File(remoteRoot, uri);
		checkRepositoryDoesntExists(file);
		processor.process(makeRequestLine(CommonConstants.POST, "/" + CommonConstants.makeRootPrefix + "/" + uri), makeDataMap(Collections.<String, Object> emptyMap()));
		checkRepositoryExists(file, uri);
	}

	@Override
	protected MakeRootProcessor makeProcessor() {
		return new MakeRootProcessor(new UrlCache<String>(), remoteOperations);
	}

}
