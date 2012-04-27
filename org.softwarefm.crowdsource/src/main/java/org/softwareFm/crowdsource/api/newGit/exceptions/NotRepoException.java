package org.softwareFm.crowdsource.api.newGit.exceptions;

import org.softwareFm.crowdsource.constants.GitMessages;

public class NotRepoException extends RepoException{
	NotRepoException(String url) {
		super(GitMessages.notRepo, url);
	}

}
