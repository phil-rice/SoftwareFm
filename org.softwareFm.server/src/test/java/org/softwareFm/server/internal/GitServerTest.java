package org.softwareFm.server.internal;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitServer;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.tests.Tests;

public class GitServerTest extends GitTest {

	IGitServer gitServer;

	public void testPostAddsToRepository() throws Exception {
		checkCreateRepository(gitServer, "a/b/c");
		gitServer.post(IFileDescription.Utils.plain("a/b/c"), v11);

		// This is cloning a new repository and checking the data got there
		File targetDirectory = new File(root, "target");
		Git call = Git.cloneRepository().//
				setDirectory(targetDirectory).//
				setURI(new File(root, "a/b/c").getAbsolutePath()).//
				call();
		call.getRepository().close();
		assertEquals(v11, Json.mapFromString(Files.getText(new File(targetDirectory, "data.json"))));
	}

	public void testCreateRepository() {
		checkCreateRepository(gitServer, "a/b/c");
		checkCreateRepository(gitServer, "a/b/d");
		checkCreateRepository(gitServer, "a/b/e");
		checkCreateRepository(gitServer, "a/f");
		checkCreateRepository(gitServer, "b");
	}

	public void testFindRepositoryUrl() {
		checkCreateRepository(gitServer, "a/b");
		assertEquals(new File(root, "a/b"), gitServer.findRepositoryUrl("a/b/c"));
	}

	// TODO Write testCloneOrPull
	public void testCloneOrPull() {
	}

	public void testCannotCreateUnderExistingRepository() {
		checkCreateRepository(gitServer, "a/b");
		RuntimeException e = Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				gitServer.createRepository("a/b/c");
			}
		});
		assertEquals("Cannot create git a/b/c under second repository", e.getMessage());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gitServer = makeGitServer("Not used");
	}

}
