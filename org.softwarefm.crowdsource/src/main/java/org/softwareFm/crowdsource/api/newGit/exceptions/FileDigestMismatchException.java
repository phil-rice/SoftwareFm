package org.softwareFm.crowdsource.api.newGit.exceptions;

import org.softwareFm.crowdsource.constants.SecurityMessages;

public class FileDigestMismatchException extends RepoSecurityException {

	public FileDigestMismatchException(String rl, String expectedDigest, String actualDigest) {
		super(SecurityMessages.fileDigestMismatch, rl, expectedDigest, actualDigest);
	}

}
