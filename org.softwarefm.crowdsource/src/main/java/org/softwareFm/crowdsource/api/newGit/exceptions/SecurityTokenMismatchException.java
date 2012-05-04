package org.softwareFm.crowdsource.api.newGit.exceptions;

import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.newGit.facard.SecurityToken;
import org.softwareFm.crowdsource.constants.SecurityMessages;

public class SecurityTokenMismatchException extends RepoSecurityException {

	public SecurityTokenMismatchException(UserData userData, String rl, SecurityToken expectedToken, SecurityToken token) {
		super(SecurityMessages.securityMismatchException, userData, rl, expectedToken, token);
		
	}

}
