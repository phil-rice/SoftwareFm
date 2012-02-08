package org.softwareFm.server.processors.internal;

import org.softwareFm.server.processors.AbstractLoginSignupForgotCheckerTest;

public class SignUpCheckerTest extends AbstractLoginSignupForgotCheckerTest {

	public void testAddingLegalEmails() {
		checkSignup("email1", "moniker1", "salt1", "hash1", "sfmId1");
		checkSignup("email2", "moniker2", "salt2", "hash2", "sfmId2");
	}

	public void testAddingDuplicateEmails() {
		checkSignup("email1", "moniker1", "salt1", "hash1", "sfmId1");
		checkSignup("email2", "moniker2", "salt2", "hash2", "sfmId2");
		checkNotAdded("email1", "salt2", "hash2", "sfmId2");
	}

}
