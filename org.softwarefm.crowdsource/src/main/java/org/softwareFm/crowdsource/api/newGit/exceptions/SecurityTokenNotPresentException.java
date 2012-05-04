package org.softwareFm.crowdsource.api.newGit.exceptions;

public class SecurityTokenNotPresentException extends RepoSecurityException{

	public SecurityTokenNotPresentException(String pattern, Object... args) {
		super(pattern, args);
	}

}
