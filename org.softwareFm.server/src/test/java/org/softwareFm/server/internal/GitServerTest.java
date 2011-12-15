package org.softwareFm.server.internal;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepository;
import org.junit.Test;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.tests.Tests;

public class GitServerTest extends GitTest {

	private GitServer gitServer;

	@Test
	public void testCreateRepository() {
		checkCreateRepository("a/b/c");
		checkCreateRepository("a/b/d");
		checkCreateRepository("a/b/e");
		checkCreateRepository("a/f");
		checkCreateRepository("b");
	}

	public void testFindRepositoryUrl() {
		checkCreateRepository("a/b");
		assertEquals(new File(root, "a/b"), gitServer.findRepositoryUrl("a/b/c"));
	}

	public void testCommit() throws Exception {
		checkCreateRepository("a/b/c");
		gitServer.post("a/b/c", v11);

		File targetDirectory = new File(root, "target");
		Git.cloneRepository().//
				setDirectory(targetDirectory).//
				setURI(new File(root, "a/b/c").getAbsolutePath()).//
				call();
		assertEquals(v11, Json.mapFromString(Files.getText(new File(targetDirectory, "data.json"))));
	}

	public void testCannotCreateUnderExistingRepository() {
		checkCreateRepository("a/b");
		RuntimeException e = Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				gitServer.createRepository("a/b/c");
			}
		});
		assertEquals("Cannot create git a/b/c under second repository", e.getMessage());
	}

	private void checkCreateRepository(String url) {
		gitServer.createRepository(url);
		FileRepository fileRepository = IGitServer.Utils.makeFileRepository(root, url);
		assertEquals(new File(root, url + "/" + ServerConstants.gitDirectory), fileRepository.getDirectory());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gitServer = (GitServer) localGitClient;
	}

	@Override
	protected LocalGitClient makeLocalGitClient() {
		return new GitServer(root);
	}

}
