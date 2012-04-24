package org.softwareFm.crowdsource.api.newGit.internal;

import org.softwareFm.crowdsource.api.newGit.facard.CannotUseRepoAfterCommitOrRollbackException;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class RepoPrimGlobalTest extends AbstractRepoPrimTest {
	public void testMethodsDontWorkAfterCommit() {
		repoPrim.commit();
		Tests.assertThrowsWithMessage("Cannot use after Commit or Rollback. Method read Args [RawSingleSource [fullRl=a/b/x/data.txt]]", CannotUseRepoAfterCommitOrRollbackException.class, new Runnable() {
			@Override
			public void run() {
				repoPrim.read(abxSource);
			}
		});
		Tests.assertThrowsWithMessage("Cannot use after Commit or Rollback. Method read Args [RawSingleSource [fullRl=a/b/x/data.txt]]", CannotUseRepoAfterCommitOrRollbackException.class, new Runnable() {
			@Override
			public void run() {
				repoPrim.append(abxSource, v21);
			}
		});
		Tests.assertThrowsWithMessage("Cannot use after Commit or Rollback. Method delete Args [1]", CannotUseRepoAfterCommitOrRollbackException.class, new Runnable() {
			@Override
			public void run() {
				repoPrim.delete(abxSource, 1);
			}
		});
		Tests.assertThrowsWithMessage("Cannot use after Commit or Rollback. Method change Args [1, {c=3, v=1}]", CannotUseRepoAfterCommitOrRollbackException.class, new Runnable() {
			@Override
			public void run() {
				repoPrim.change(abxSource, 1, v31);
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		abxFile = "a/b/x/data.txt";
		acxFile = "a/c/x/data.txt";
		abxSource = new RawSingleSource(abxFile);
		acxSouce = new RawSingleSource(acxFile);
	}

	@Override
	protected void putFile(String rl, String lines) {
		gitFacard.putFileReturningRepoRl(rl, lines);

	}
}
