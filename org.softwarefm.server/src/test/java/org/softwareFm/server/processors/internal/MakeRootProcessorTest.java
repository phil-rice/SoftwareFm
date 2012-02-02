package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Collections;

import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.maps.UrlCache;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.server.processors.IProcessResult;

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

	public void testOKWhenRepositoryAlreadyExists() {
		checkProcessMakesRepo("a/b/c");
		checkProcessRemakeRepo("a/b/c");
		checkProcessMakesRepo("b/d");
		checkProcessRemakeRepo("b/d");
	}

	public void testThrowsExceptionWhenTryingToCreateUnderNewRepository() {
		checkProcessMakesRepo("a/b");
		IProcessResult result = processor.process(makeRequestLine(CommonConstants.POST, "/" + CommonConstants.makeRootPrefix + "/" + "a/b/c"), makeDataMap(Collections.<String, Object> emptyMap()));
		checkErrorResult(result, CommonConstants.notFoundStatusCode, "Cannot create git /a/b/c under second repository","Cannot create git /a/b/c under second repository");

		checkRepositoryDoesntExists(new File(remoteRoot, "a/b/c"));
	}

	private void checkProcessRemakeRepo(String uri) {
		checkRepositoryExists(remoteRoot, uri);
		processor.process(makeRequestLine(CommonConstants.POST, "/" + CommonConstants.makeRootPrefix + "/" + uri), makeDataMap(Collections.<String, Object> emptyMap()));
		checkRepositoryExists(remoteRoot, uri);
	}

	private void checkProcessMakesRepo(String uri) {
		File file = new File(remoteRoot, uri);
		checkRepositoryDoesntExists(file);
		processor.process(makeRequestLine(CommonConstants.POST, "/" + CommonConstants.makeRootPrefix + "/" + uri), makeDataMap(Collections.<String, Object> emptyMap()));
		checkRepositoryExists(remoteRoot, uri);
	}

	@Override
	protected MakeRootProcessor makeProcessor() {
		return new MakeRootProcessor(new UrlCache<String>(), remoteOperations);
	}

}
