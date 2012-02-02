package org.softwareFm.server.processors.internal;

import org.softwareFm.server.processors.AbstractLoginSignupForgotCheckerTest;

public class LoginCheckerTest extends AbstractLoginSignupForgotCheckerTest {

	public void testLoginWhenExists() {
		String crypto1 = checkSignup("email1", "salt1", "hash1", "sfmId1");
		String crypto2 = checkSignup("email2", "salt2", "hash2", "sfmId2");
		checkLogin("email1", "hash1", crypto1, "sfmId1");
		checkLogin("email2", "hash2", crypto2, "sfmId2");
	}

	public void testLoginWhenDoesntExists() {
		String crypto1 = checkSignup("email1", "salt1", "hash1", "sfmId1");
		checkSignup("email2", "salt2", "hash2", "sfmId2");

		checkCannotLogin("email1", "unknown");
		checkLogin("email1", "hash1", crypto1, "sfmId1");
	}

}
