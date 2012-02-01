package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Collections;

import org.junit.Test;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.server.processors.AbstractProcessCallTest;

public class DeleteProcessorTest extends AbstractProcessCallTest<DeleteProcessor> {

	@Test
	public void testIgnoresNoneDelete() {
		checkIgnores(CommonConstants.POST);
		checkIgnores(CommonConstants.GET);
	}

	public void testDeletesFileAndCommitsTheDeletion() {
		checkCreateRepository(remoteOperations, "a/b");
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v11);
		checkContents(remoteRoot, "a/b/c", v11);

		processor.process(makeRequestLine(CommonConstants.DELETE, "a/b/c"), Collections.<String, Object> emptyMap());

		checkContentsDontExist(remoteRoot, "a/b/c");
		
		createAndPull("a/b");
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
		return new DeleteProcessor(remoteOperations);
	}

}
