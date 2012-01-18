package org.softwareFm.server.processors.internal;

public class LoginCheckerTest extends AbstractLoginSignupForgotCheckerTest {

	public void testLoginWhenExists() {
		String crypto1 = checkSignup("email1", "salt1", "hash1");
		String crypto2 = checkSignup("email2", "salt2", "hash2");
		checkLogin("email1", "hash1", crypto1);
		checkLogin("email2", "hash2", crypto2);
	}

	public void testLoginWhenDoesntExists() {
		String crypto1 = checkSignup("email1", "salt1", "hash1");
		checkSignup("email2", "salt2", "hash2");

		checkCannotLogin("email1", "unknown");
		checkLogin("email1", "hash1", crypto1);
	}

}
