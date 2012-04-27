package org.softwareFm.crowdsource.api.newGit.exceptions;

import org.softwareFm.crowdsource.constants.GitMessages;

public class NotUnderRepoException extends RepoException{
public 	NotUnderRepoException(String rl) {
		super(GitMessages.notUnderRepo, rl);
	}
}
