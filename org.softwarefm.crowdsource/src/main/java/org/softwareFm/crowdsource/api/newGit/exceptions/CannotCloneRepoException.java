package org.softwareFm.crowdsource.api.newGit.exceptions;

import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.constants.GitMessages;

public class CannotCloneRepoException extends RepoException {

	public CannotCloneRepoException(String remoteRepoRl, RepoLocation repoLocation) {
		super(GitMessages.cannotCloneRepo, remoteRepoRl, repoLocation);
	}

}
