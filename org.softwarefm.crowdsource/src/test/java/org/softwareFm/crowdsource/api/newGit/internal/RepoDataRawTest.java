package org.softwareFm.crowdsource.api.newGit.internal;

import org.softwareFm.crowdsource.api.newGit.facard.CannotUseRepoAfterCommitOrRollbackException;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class RepoDataRawTest extends AbstractRepoDataTest {
	
	public void testMethodsDontWorkAfterCommit() {
		repoData.commit();
		Tests.assertThrowsWithMessage("Cannot use after Commit or Rollback. Method read Args [RawSingleSource [fullRl=a/b/x/data.txt]]", CannotUseRepoAfterCommitOrRollbackException.class, new Runnable() {
			@Override
			public void run() {
				repoData.readRaw(abxSource);
			}
		});
		Tests.assertThrowsWithMessage("Cannot use after Commit or Rollback. Method read Args [RawSingleSource [fullRl=a/b/x/data.txt]]", CannotUseRepoAfterCommitOrRollbackException.class, new Runnable() {
			@Override
			public void run() {
				repoData.append(abxSource, v21);
			}
		});
		Tests.assertThrowsWithMessage("Cannot use after Commit or Rollback. Method delete Args [1]", CannotUseRepoAfterCommitOrRollbackException.class, new Runnable() {
			@Override
			public void run() {
				repoData.delete(abxSource, 1);
			}
		});
		Tests.assertThrowsWithMessage("Cannot use after Commit or Rollback. Method change Args [1, {c=3, v=1}]", CannotUseRepoAfterCommitOrRollbackException.class, new Runnable() {
			@Override
			public void run() {
				repoData.change(abxSource, 1, v31);
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		abxSource = new RawSingleSource(abxFile);
		abySource = new RawSingleSource(abyFile);
		acxSource = new RawSingleSource(acxFile);
		acySource = new RawSingleSource(acyFile);
	}

	@Override
	protected void putFile(String rl, String lines) {
		gitFacard.putFileReturningRepoRl(rl, lines);

	}
}
