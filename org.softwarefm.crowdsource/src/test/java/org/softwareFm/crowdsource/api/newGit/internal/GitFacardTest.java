package org.softwareFm.crowdsource.api.newGit.internal;

import java.io.File;
import java.nio.channels.FileLock;

import org.eclipse.jgit.storage.file.FileRepository;
import org.softwareFm.crowdsource.api.newGit.facard.RepoRlAndText;
import org.softwareFm.crowdsource.api.newGit.facard.TryingToLockUnderRepoException;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.softwareFm.crowdsource.utilities.transaction.RedoTransactionException;

public class GitFacardTest extends RepoTest {

	private File abLockFile;
	private File acLockFile;
	private GitFacard gitFacard;
	private FileLock abLock;
	private FileLock acLock;
	private File abDir;
	private File acDir;

	public void testInitCreatesDotGitDirectory() {
		// should also create repo, but that is implicitly tested by later stuff
		gitFacard.init("a/b");
		File abDotGit = new File(new File(remoteRoot, "a/b"), CommonConstants.DOT_GIT);
		assertTrue(abDotGit.exists());
		assertTrue(abDotGit.isDirectory());
	}

	public void testPutFileCreatesFileWithContentEqualToTextAndReturnsRepoRl() {
		String text = "some\ntext";
		gitFacard.init("a");
		String repoRl = gitFacard.putFileReturningRepoRl("a/b/data.text", text);
		File dataFile = new File(abDir, "data.text");
		assertEquals(text, Files.getText(dataFile));
		assertEquals("a", repoRl);
	}

	public void testGetFileReturnsContentOfFile() {
		String text = "some\ntext";
		gitFacard.init("a");
		File dataFile = new File(abDir, "data.text");
		Files.makeDirectoryForFile(dataFile);
		Files.setText(dataFile, text);

		assertEquals(new RepoRlAndText("a", text), gitFacard.getFile("a/b/data.text"));
	}

	public void testGetFileReturnsEmptyStringIfNotIn() {
		gitFacard.init("a");
		File dataFile = new File(abDir, "data.text");
		Files.makeDirectoryForFile(dataFile);
		assertEquals(new RepoRlAndText("a", ""), gitFacard.getFile("a/b/data.text"));
		assertFalse(dataFile.exists());
	}

	public void testAddAllAndRollbackRemovesNewFiles() {
		gitFacard.init("a");
		gitFacard.putFileReturningRepoRl("a/b/c.txt", "a/b/c");
		gitFacard.putFileReturningRepoRl("a/b/d.txt", "a/b/d");

		FileRepository fileRepository = gitFacard.addAll("a");
		gitFacard.rollback(fileRepository);

		assertFalse(new File(new File(remoteRoot, "a/b"), "c.txt").exists());
		assertFalse(new File(new File(remoteRoot, "a/b"), "d.txt").exists());
	}

	public void testAddAllAndCommitLeavesFilesInPlaces() {
		// not sure how to check they got to the repository, so the next test kind of tests that
		gitFacard.init("a");
		gitFacard.putFileReturningRepoRl("a/b/c.txt", "a/b/c");
		gitFacard.putFileReturningRepoRl("a/b/d.txt", "a/b/d");
		FileRepository fileRepository = gitFacard.addAll("a");
		gitFacard.commit(fileRepository, "some message");

		assertEquals(new RepoRlAndText("a", "a/b/c"), gitFacard.getFile("a/b/c.txt"));
		assertEquals(new RepoRlAndText("a", "a/b/d"), gitFacard.getFile("a/b/d.txt"));
	}

	public void testAddAllAndCommitThenRollbackLeavesFilesAsTheyWereAfterTheFirstCommit() {
		// not sure how to check they got to the repository, so the next test kind of tests that
		gitFacard.init("a");
		gitFacard.putFileReturningRepoRl("a/b/c.txt", "a/b/c");
		gitFacard.putFileReturningRepoRl("a/b/d.txt", "a/b/d");
		FileRepository fileRepository = gitFacard.addAll("a");
		gitFacard.commit(fileRepository, "some message");

		assertEquals(new RepoRlAndText("a", "a/b/c"), gitFacard.getFile("a/b/c.txt"));
		assertEquals(new RepoRlAndText("a", "a/b/d"), gitFacard.getFile("a/b/d.txt"));

		gitFacard.putFileReturningRepoRl("a/b/c.txt", "a/b/c_changed");
		gitFacard.putFileReturningRepoRl("a/b/d.txt", "a/b/d_changed");

		FileRepository fileRepository2 = gitFacard.addAll("a");
		gitFacard.rollback(fileRepository2);

		assertEquals(new RepoRlAndText("a", "a/b/c"), gitFacard.getFile("a/b/c.txt"));
		assertEquals(new RepoRlAndText("a", "a/b/d"), gitFacard.getFile("a/b/d.txt"));
	}

	public void testLockCreatesAFile() {
		abLock = gitFacard.lock("a/b");
		acLock = gitFacard.lock("a/c");

		assertTrue(abLockFile.exists());
		assertTrue(acLockFile.exists());
	}

	public void testCannotLockUnderARepo() {
		gitFacard.init("a");
		Tests.assertThrowsWithMessage("Url a/b is not a valid repo url, as it is under a", TryingToLockUnderRepoException.class, new Runnable() {
			@Override
			public void run() {
				gitFacard.lock("a/b");
			}
		});
	}

	public void testCanLockUnlockThenLock() {
		abLock = gitFacard.lock("a/b");
		gitFacard.unLock(abLock);
		abLock = gitFacard.lock("a/b");
	}

	public void testTryingToRegetALockThrowsRedoTransactionException() {
		abLock = gitFacard.lock("a/b");
		Tests.assertThrowsWithMessage("a/b already locked", RedoTransactionException.class, new Runnable() {
			@Override
			public void run() {
				gitFacard.lock("a/b");
			}
		});
		gitFacard.unLock(abLock);
		abLock = gitFacard.lock("a/b");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gitFacard = new GitFacard(remoteRoot);
		abDir = new File(remoteRoot, "a/b");
		acDir = new File(remoteRoot, "a/c");
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
