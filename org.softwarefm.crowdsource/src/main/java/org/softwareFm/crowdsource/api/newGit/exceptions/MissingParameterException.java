package org.softwareFm.crowdsource.api.newGit.exceptions;

public class MissingParameterException extends RepoSecurityException{

	public MissingParameterException(String pattern, Object... args) {
		super(pattern, args);
	}

}
