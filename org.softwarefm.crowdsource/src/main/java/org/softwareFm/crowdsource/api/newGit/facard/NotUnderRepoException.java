package org.softwareFm.crowdsource.api.newGit.facard;

import org.softwareFm.crowdsource.constants.GitMessages;

public class NotUnderRepoException extends RepoException{
public 	NotUnderRepoException(String url) {
		super(GitMessages.notUnderRepo, url);
	}
}
