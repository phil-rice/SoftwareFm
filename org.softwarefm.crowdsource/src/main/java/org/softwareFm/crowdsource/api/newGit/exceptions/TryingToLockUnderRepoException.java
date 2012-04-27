package org.softwareFm.crowdsource.api.newGit.exceptions;

import org.softwareFm.crowdsource.constants.GitMessages;

public class TryingToLockUnderRepoException extends RepoException {

	public TryingToLockUnderRepoException(String existingRepo, String repoRl) {
		super(GitMessages.tryingToLockUnderRepo, repoRl, existingRepo);
	}

}
