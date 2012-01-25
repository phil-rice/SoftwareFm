package org.softwareFm.server.processors.internal;

import org.softwareFm.utilities.crypto.Crypto;

public class PasswordResetterTest extends AbstractLoginSignupForgotCheckerTest {

	public void testRosyView() {
		checkSignup("email1@a", "salt", "initialHash", "sfmId1");
		String magicString = checkSendPasswordEmail("email1@a");
		String newPassword = resetPassword.reset(magicString);

		String newDigest = Crypto.digest("salt", newPassword);
		String actualNewDigest = template.queryForObject("select password from users where email =?", String.class, new Object[] { "email1@a" });
		assertEquals(newDigest, actualNewDigest);
	}

	public void testWhenMagicStringNotThere() {
		String magicString = "not in";
		String newPassword = resetPassword.reset(magicString);
		assertNull(newPassword);
	}
}
