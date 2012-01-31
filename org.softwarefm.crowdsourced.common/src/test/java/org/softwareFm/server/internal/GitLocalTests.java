package org.softwareFm.server.internal;

import java.util.Map;

import org.softwareFm.server.GitTest;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.utilities.maps.Maps;

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
