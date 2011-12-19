package org.softwareFm.server.internal;

import java.io.File;

import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.tests.Tests;

public class GitFacardTest extends GitTest {

	private File local;
	private File remote;
	private String remoteAsUri;

	public void testCreateRepository() {
		gitFacard.createRepository(root, "remote");
		checkRepositoryExists(remote);
		assertEquals("master", gitFacard.getBranch(root, "remote"));
	}

	public void testCreateRepositoryWithSlash() {
		checkRepositoryDoesntExists(remote);
		gitFacard.createRepository(root, "/remote");
		checkRepositoryExists(remote);
		assertEquals("master", gitFacard.getBranch(root, "remote"));
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
		Files.setText(new File(remote, "someData.txt"), "someValue");
		gitFacard.addAllAndCommit(root, "remote", "auto");
		gitFacard.clone(remoteAsUri, root, "local");
		assertEquals("master", gitFacard.getBranch(root, "local"));
		checkRepositoryExists(local);
		assertEquals("someValue", Files.getText(new File(local, "someData.txt")));
	}

	public void testAddAllAndCommitFollowedByCloneDuplicatesFileWithSlash() {
		gitFacard.createRepository(root, "/remote");
		Files.setText(new File(remote, "/someData.txt"), "someValue");
		gitFacard.addAllAndCommit(root, "/remote", "auto");
		gitFacard.clone(remoteAsUri, root, "/local");
		assertEquals("master", gitFacard.getBranch(root, "local"));
		checkRepositoryExists(local);
		assertEquals("someValue", Files.getText(new File(local, "someData.txt")));
	}

	public void testPullDuplicatesData() {
		gitFacard.createRepository(root, "remote");
		gitFacard.clone(remoteAsUri, root, "local");

		Files.setText(new File(remote, "someData.txt"), "someValue");
		gitFacard.addAllAndCommit(root, "remote", "auto");

		gitFacard.pull(root, "local");
		assertEquals("someValue", Files.getText(new File(local, "someData.txt")));
	}

	public void testPullDuplicatesDataWithSlash() {
		gitFacard.createRepository(root, "/remote");
		gitFacard.clone(remoteAsUri, root, "/local");

		Files.setText(new File(remote, "someData.txt"), "someValue");
		gitFacard.addAllAndCommit(root, "remote", "auto");

		gitFacard.pull(root, "/local");
		assertEquals("someValue", Files.getText(new File(local, "someData.txt")));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		local = new File(root, "local");
		remote = new File(root, "remote");
		remoteAsUri = new File(root, "remote").getAbsolutePath();
	}

}
