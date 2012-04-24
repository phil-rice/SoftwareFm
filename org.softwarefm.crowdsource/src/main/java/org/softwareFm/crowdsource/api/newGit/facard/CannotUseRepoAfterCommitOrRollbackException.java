package org.softwareFm.crowdsource.api.newGit.facard;

import java.util.Arrays;

import org.softwareFm.crowdsource.constants.GitMessages;

public class CannotUseRepoAfterCommitOrRollbackException extends RepoException{

	public CannotUseRepoAfterCommitOrRollbackException(String methodName, Object... args) {
		super(GitMessages.cannotUseRepoAfterCommitOrRollback, methodName, Arrays.asList(args));
	}

}
