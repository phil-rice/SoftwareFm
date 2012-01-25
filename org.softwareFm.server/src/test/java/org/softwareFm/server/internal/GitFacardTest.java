package org.softwareFm.server.internal;

import java.io.File;

import org.softwareFm.server.GitTest;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.tests.Tests;
import org.softwareFm.utilities.url.Urls;

public class GitFacardTest extends GitTest {

	public void testCreateRepository() {
		gitFacard.createRepository(root, "remote");
		checkRepositoryExists(remoteRoot);
		assertEquals("master", gitFacard.getBranch(root, "remote"));
	}

	public void testCreateRepositoryWithSlash() {
		checkRepositoryDoesntExists(remoteRoot);
		gitFacard.createRepository(root, "/remote");
		checkRepositoryExists(remoteRoot);
		assertEquals("master", gitFacard.getBranch(root, "remote"));
	}

	public void testDeleteWhenExistsLocally() {
		gitFacard.createRepository(root, "/remote");
		put(remoteRoot, "a/b/c", v11);
		put(remoteRoot, "a/b/c/d", v12);
		gitFacard.addAllAndCommit(remoteRoot, IFileDescription.Utils.plain("a/b"), "message");
		gitFacard.clone(remoteAsUri, root, "/local");
		File localAbc = new File(localRoot, Urls.compose("a/b/c", ServerConstants.dataFileName));
		File remoteAbc = new File(remoteRoot, Urls.compose("a/b/c", ServerConstants.dataFileName));

		assertTrue(localAbc.exists());
		assertTrue(remoteAbc.exists());
		assertEquals(v11, Json.mapFromString(Files.getText(localAbc)));

		gitFacard.delete(remoteRoot, "a/b/c");

		assertEquals(v11, Json.mapFromString(Files.getText(localAbc)));
		assertFalse(remoteAbc.exists());

		gitFacard.pull(localRoot, "a/b");

		assertFalse(localAbc.exists());
		assertFalse(remoteAbc.exists());

		assertEquals(v12, Json.mapFromString(Files.getText(new File(localRoot, Urls.compose("a/b/c/d", ServerConstants.dataFileName)))));

	}

	public void testCreateRepositoryThrowsExceptionIfParentRepositoryOfUrlExists() {
		gitFacard.createRepository(root, "remote");
		IllegalArgumentException e = Tests.assertThrows(IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				gitFacard.createRepository(root, "/remote/a");
			}
		});
		assertEquals("Cannot create git /remote/a under second repository", e.getMessage());
	}

	public void testCreateSetsConfigOptions() {
		gitFacard.createRepository(root, "remote");
	}

	public void testCreateSetsConfigOptionsWithSlash() {
		gitFacard.createRepository(root, "/remote");
	}

	public void testCloneSetsConfigOptions() {
		gitFacard.createRepository(root, "remote");
		gitFacard.clone(remoteAsUri, root, "local");

		assertEquals("origin", gitFacard.getConfig(root, "local", "branch", "master", "remote"));
		assertEquals("refs/heads/master", gitFacard.getConfig(root, "local", "branch", "master", "merge"));
	}

	public void testCloneSetsConfigOptionsWithSlash() {
		gitFacard.createRepository(root, "/remote");
		gitFacard.clone(remoteAsUri, root, "/local");

		assertEquals("origin", gitFacard.getConfig(root, "local", "branch", "master", "remote"));
		assertEquals("refs/heads/master", gitFacard.getConfig(root, "local", "branch", "master", "merge"));
	}

	public void testAddAllAndCommitFollowedByCloneDuplicatesFile() {
		gitFacard.createRepository(root, "remote");
		Files.setText(new File(remoteRoot, "someData.txt"), "someValue");
		gitFacard.addAllAndCommit(root, IFileDescription.Utils.plain("remote"), "auto");
		gitFacard.clone(remoteAsUri, root, "local");
		assertEquals("master", gitFacard.getBranch(root, "local"));
		checkRepositoryExists(localRoot);
		assertEquals("someValue", Files.getText(new File(localRoot, "someData.txt")));
	}

	public void testAddAllAndCommitFollowedByCloneDuplicatesFileWithSlash() {
		gitFacard.createRepository(root, "/remote");
		Files.setText(new File(remoteRoot, "/someData.txt"), "someValue");
		gitFacard.addAllAndCommit(root, IFileDescription.Utils.plain("/remote"), "auto");
		gitFacard.clone(remoteAsUri, root, "/local");
		assertEquals("master", gitFacard.getBranch(root, "local"));
		checkRepositoryExists(localRoot);
		assertEquals("someValue", Files.getText(new File(localRoot, "someData.txt")));
	}

	public void testPullDuplicatesData() {
		gitFacard.createRepository(root, "remote");
		gitFacard.clone(remoteAsUri, root, "local");

		Files.setText(new File(remoteRoot, "someData.txt"), "someValue");
		gitFacard.addAllAndCommit(root, IFileDescription.Utils.plain("remote"), "auto");

		gitFacard.pull(root, "local");
		assertEquals("someValue", Files.getText(new File(localRoot, "someData.txt")));
	}

	public void testPullDuplicatesDataWithSlash() {
		gitFacard.createRepository(root, "/remote");
		gitFacard.clone(remoteAsUri, root, "/local");

		Files.setText(new File(remoteRoot, "someData.txt"), "someValue");
		gitFacard.addAllAndCommit(root, IFileDescription.Utils.plain("remote"), "auto");

		gitFacard.pull(root, "/local");
		assertEquals("someValue", Files.getText(new File(localRoot, "someData.txt")));
	}

	public static void main(String[] args) {
		while (true)
			Tests.executeTest(GitFacardTest.class);
	}
}
