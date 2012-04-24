package org.softwareFm.crowdsource.api.newGit.facard;

import org.softwareFm.crowdsource.constants.GitMessages;

public class AlreadyUnderRepoException extends RepoException {

	public AlreadyUnderRepoException(String existingRepoUrl, String url) {
		super(GitMessages.alreadyUnderRepo, url, existingRepoUrl);
	}

}
