package org.softwareFm.crowdsource.api.newGit.internal;

import java.io.File;
import java.nio.channels.FileLock;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.crowdsource.api.newGit.exceptions.TryingToLockUnderRepoException;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.api.newGit.facard.RepoRlAndText;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.crowdsource.utilities.transaction.RedoTransactionException;

public class GitFacardTest extends RepoTest {

	private File abLockFile;
	private File acLockFile;
	private FileLock abLock;
	private FileLock acLock;
	private File abDir;
	private File acDir;



	public void testInitCreatesDotGitDirectory() {
		// should also create repo, but that is implicitly tested by later stuff
		linkedFacard.init("a/b");
		File abDotGit = new File(new File(localRoot, "a/b"), CommonConstants.DOT_GIT);
		assertTrue(abDotGit.exists());
		assertTrue(abDotGit.isDirectory());
	}

	public void testPutFileCreatesFileWithContentEqualToTextAndReturnsRepoRl() {
		String text = "some\ntext";
		linkedFacard.init("a");
		String repoRl = linkedFacard.putFileReturningRepoRl("a/b/data.text", text);
		File dataFile = new File(abDir, "data.text");
		assertEquals(text, Files.getText(dataFile));
		assertEquals("a", repoRl);
	}

	public void testGetFileReturnsContentOfFile() {
		String text = "some\ntext";
		linkedFacard.init("a");
		File dataFile = new File(abDir, "data.text");
		Files.makeDirectoryForFile(dataFile);
		Files.setText(dataFile, text);

		assertEquals(new RepoRlAndText("a", text), linkedFacard.getFile("a/b/data.text"));
	}

	public void testGetFileReturnsEmptyStringIfNotIn() {
		linkedFacard.init("a");
		File dataFile = new File(abDir, "data.text");
		Files.makeDirectoryForFile(dataFile);
		assertEquals(new RepoRlAndText("a", ""), linkedFacard.getFile("a/b/data.text"));
		assertFalse(dataFile.exists());
	}

	public void testAddAllAndRollbackRemovesNewFiles() {
		linkedFacard.init("a");
		linkedFacard.putFileReturningRepoRl("a/b/c.txt", "a/b/c");
		linkedFacard.putFileReturningRepoRl("a/b/d.txt", "a/b/d");

		FileRepository fileRepository = linkedFacard.addAll("a");
		linkedFacard.rollback(fileRepository);

		assertFalse(new File(new File(remoteRoot, "a/b"), "c.txt").exists());
		assertFalse(new File(new File(remoteRoot, "a/b"), "d.txt").exists());
	}

	public void testAddAllAndCommitLeavesFilesInPlaces() {
		// not sure how to check they got to the repository, so the next test kind of tests that
		linkedFacard.init("a");
		linkedFacard.putFileReturningRepoRl("a/b/c.txt", "a/b/c");
		linkedFacard.putFileReturningRepoRl("a/b/d.txt", "a/b/d");
		FileRepository fileRepository = linkedFacard.addAll("a");
		linkedFacard.commit(fileRepository, "some message");

		assertEquals(new RepoRlAndText("a", "a/b/c"), linkedFacard.getFile("a/b/c.txt"));
		assertEquals(new RepoRlAndText("a", "a/b/d"), linkedFacard.getFile("a/b/d.txt"));
	}

	public void testAddAllAndCommitThenRollbackLeavesFilesAsTheyWereAfterTheFirstCommit() {
		// not sure how to check they got to the repository, so the next test kind of tests that
		linkedFacard.init("a");
		linkedFacard.putFileReturningRepoRl("a/b/c.txt", "a/b/c");
		linkedFacard.putFileReturningRepoRl("a/b/d.txt", "a/b/d");
		FileRepository fileRepository = linkedFacard.addAll("a");
		linkedFacard.commit(fileRepository, "some message");

		assertEquals(new RepoRlAndText("a", "a/b/c"), linkedFacard.getFile("a/b/c.txt"));
		assertEquals(new RepoRlAndText("a", "a/b/d"), linkedFacard.getFile("a/b/d.txt"));

		linkedFacard.putFileReturningRepoRl("a/b/c.txt", "a/b/c_changed");
		linkedFacard.putFileReturningRepoRl("a/b/d.txt", "a/b/d_changed");

		FileRepository fileRepository2 = linkedFacard.addAll("a");
		linkedFacard.rollback(fileRepository2);

		assertEquals(new RepoRlAndText("a", "a/b/c"), linkedFacard.getFile("a/b/c.txt"));
		assertEquals(new RepoRlAndText("a", "a/b/d"), linkedFacard.getFile("a/b/d.txt"));
	}

	public void testLockCreatesAFile() {
		abLock = linkedFacard.lock("a/b");
		acLock = linkedFacard.lock("a/c");

		assertTrue(abLockFile.exists());
		assertTrue(acLockFile.exists());
	}

	public void testCannotLockUnderARepo() {
		linkedFacard.init("a");
		Tests.assertThrowsWithMessage("Url a/b is not a valid repo url, as it is under a", TryingToLockUnderRepoException.class, new Runnable() {
			@Override
			public void run() {
				linkedFacard.lock("a/b");
			}
		});
	}

	public void testCanLockUnlockThenLock() {
		abLock = linkedFacard.lock("a/b");
		linkedFacard.unLock(abLock);
		abLock = linkedFacard.lock("a/b");
	}

	public void testTryingToRegetALockThrowsRedoTransactionException() {
		abLock = linkedFacard.lock("a/b");
		Tests.assertThrowsWithMessage("RepoRl a/b already locked", RedoTransactionException.class, new Runnable() {
			@Override
			public void run() {
				linkedFacard.lock("a/b");
			}
		});
		linkedFacard.unLock(abLock);
		abLock = linkedFacard.lock("a/b");
	}

	public void testClone() {
		initRepos(remoteFacard, "a/b");
		put(remoteRoot, "a/b/c", v11);
		put(remoteRoot, "a/b/d", v12);
		addAllAndCommit(remoteFacard, "a/b");

		IGitFacard.Utils.clone(linkedFacard, "a/b");
		checkContents(localRoot, "a/b/c", v11);
		checkContents(localRoot, "a/b/d", v12);
	}

	@SuppressWarnings("unchecked")
	public void testCanReadFromMultipleRepos() {
		initRepos(linkedFacard, "a", "b");
		putFile(linkedFacard, "a/data", null, v11);
		putFile(linkedFacard, "b/data", null, v21);
		addAllAndCommit(linkedFacard, "a", "b");

		assertEquals(new RepoRlAndText("a", Json.toString(v11)), linkedFacard.getFile("a/data"));
		assertEquals(new RepoRlAndText("b", Json.toString(v21)), linkedFacard.getFile("b/data"));
	}

	@SuppressWarnings("unchecked")
	public void testCanPullFromMultipleRepos() {
		initRepos(remoteFacard, "b", "a");
		putFile(remoteFacard, "b/data", null, v21);
		putFile(remoteFacard, "a/data", null, v11);
		addAllAndCommit(remoteFacard, "b", "a");

		IGitFacard.Utils.clone(linkedFacard, "a");
		IGitFacard.Utils.clone(linkedFacard, "b");

		assertEquals(new RepoRlAndText("a", Json.toString(v11)), linkedFacard.getFile("a/data"));
		assertEquals(new RepoRlAndText("b", Json.toString(v21)), linkedFacard.getFile("b/data"));
	}

	


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		abDir = new File(localRoot, "a/b");
		acDir = new File(localRoot, "a/c");
		abLockFile = new File(abDir, CommonConstants.lockFileName);
		acLockFile = new File(acDir, CommonConstants.lockFileName);
	}

	@Override
	protected void tearDown() throws Exception {
		if (abLock != null)
			Files.releaseAndClose(abLock);
		if (acLock != null)
			Files.releaseAndClose(acLock);
		super.tearDown();
	}
}
