package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Collections;

import org.junit.Test;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.constants.CommonConstants;

public class DeleteProcessorTest extends AbstractProcessCallTest<DeleteProcessor> {

	@Test
	public void testIgnoresNoneDelete() {
		checkIgnores(ServerConstants.POST);
		checkIgnores(ServerConstants.GET);
	}

	public void testDeletesFileAndCommitsTheDeletion() {
		checkCreateRepository(remoteGitServer, "a/b");
		remoteGitServer.post(IFileDescription.Utils.plain("a/b/c"), v11);
		checkContents(remoteRoot, "a/b/c", v11);

		processor.process(makeRequestLine(ServerConstants.DELETE, "a/b/c"), Collections.<String, Object> emptyMap());

		checkContentsDontExist(remoteRoot, "a/b/c");
		localGitServer.clone("a/b");
		checkContentsDontExist(localRoot, "a/b/c");
	}

	private void checkContentsDontExist(File root, String url) {
		File directory = new File(root, url);
		File file = new File(directory, CommonConstants.dataFileName);
		assertFalse(file.exists());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected DeleteProcessor makeProcessor() {
		return new DeleteProcessor(remoteGitServer);
	}

}
