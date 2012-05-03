package org.softwareFm.crowdsource.api.newGit.internal;

import org.softwareFm.crowdsource.api.newGit.exceptions.CannotUseRepoAfterCommitOrRollbackException;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class RepoDataRawTest extends AbstractRepoDataTest {

	public void testMethodsDontWorkAfterCommit() {
		repoData.setCommitMessage("someMessage");
		repoData.commit();
		Tests.assertThrowsWithMessage("Cannot use after Commit or Rollback. Method readRaw Args [RawSingleSource [fullRl=a/b/x/data.txt]]", CannotUseRepoAfterCommitOrRollbackException.class, new Runnable() {
			@Override
			public void run() {
				repoData.readRaw(abxSource);
			}
		});
		Tests.assertThrowsWithMessage("Cannot use after Commit or Rollback. Method append Args [RawSingleSource [fullRl=a/b/x/data.txt], {c=2, v=1}]", CannotUseRepoAfterCommitOrRollbackException.class, new Runnable() {
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
	protected void putFilePrim(String rl, String lines) {
		localFacard.putFileReturningRepoRl(rl, lines);

	}
}
