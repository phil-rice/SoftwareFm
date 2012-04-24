package org.softwareFm.crowdsource.api.newGit.facard;

import org.softwareFm.crowdsource.constants.GitMessages;

public class TryingToLockUnderRepoException extends RepoException {

	public TryingToLockUnderRepoException(String existingRepo, String repoRl) {
		super(GitMessages.tryingToLockUnderRepo, repoRl, existingRepo);
	}

}
