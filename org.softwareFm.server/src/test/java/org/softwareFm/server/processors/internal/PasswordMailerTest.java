package org.softwareFm.server.processors.internal;

public class PasswordMailerTest extends AbstractLoginSignupForgotCheckerTest {

	public void testWhenEmailExists() {
		checkSignup("email1@a", "salt1", "hash1");
		checkSignup("email2@b", "salt2", "hash2");

		checkSendPasswordEmail("email1@a");
	}

	public void testWhenEmailDoesntExist() {
		checkSignup("email1@a", "salt1", "hash1");
		checkSignup("email2@b", "salt2", "hash2");

		checkCannotSendEMailPassword("email3@a");
	}

	
	
}
