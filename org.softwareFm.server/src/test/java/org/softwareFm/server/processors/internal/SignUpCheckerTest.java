package org.softwareFm.server.processors.internal;

public class SignUpCheckerTest extends AbstractLoginSignupForgotCheckerTest {


	public void testAddingLegalEmails() {
		checkSignup("email1", "salt1", "hash1");
		checkSignup("email2", "salt2", "hash2");
	}

	public void testAddingDuplicateEmails() {
		checkSignup("email1", "salt1", "hash1");
		checkSignup("email2", "salt2", "hash2");
		checkNotAdded("email1", "salt2", "hash2");
	}

}