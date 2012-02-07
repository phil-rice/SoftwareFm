package org.softwareFm.common.server.internal;

import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.common.tests.Tests;

public class GitLocalTests extends GitTest {
	public void testGetFileWhenNeedToCreate() {
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b"), v11);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/d"), v21);
	}

	public void testGetFile() {
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		checkGetFile(gitLocal, IFileDescription.Utils.plain("a"), v11);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b"), v11);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/d"), v21);
	}

	public void getGetFileAsString() {
		fail();
	}

	public void testGetFileAndDescendants() {
		Map<String, Object> map = Maps.with(v11, "c", v12, "d", v21);
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		checkGetFileAndDescendants(gitLocal, IFileDescription.Utils.plain("a/b"), map);
		checkGetFileAndDescendants(gitLocal, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFileAndDescendants(gitLocal, IFileDescription.Utils.plain("a/b/d"), v21);
	}

	public void testGetFileAboveRepo() {
		remoteOperations.init("a/b/c");
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		Tests.assertThrows(IllegalStateException.class, new Runnable() {
			@Override
			public void run() {
				gitLocal.getFile(IFileDescription.Utils.plain("a"));
			}
		});
	}

	public void testGetFileAndDescendantsAboveRepo() {
		remoteOperations.init("a/b/c");
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFileAndDescendants(gitLocal, IFileDescription.Utils.plain("a"), Maps.stringObjectMap("b", Maps.stringObjectMap("c", v12)));
	}

	public void testClearCache() {
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b"), v11);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/d"), v21);

		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v22);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b"), v11);// timeout hasn't happened, so pull doesn't actually take place
		gitLocal.clearCaches();
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b"), v22);// timeout has happened
	}
}
