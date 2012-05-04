package org.softwareFm.crowdsource.api.newGit.exceptions;

import java.text.MessageFormat;

public class RepoSecurityException extends RuntimeException {
	public RepoSecurityException(String pattern, Object... args) {
		super(MessageFormat.format(pattern, args));
	}
}
