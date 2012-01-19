package org.softwareFm.server.processors.internal;


public class EmailSaltRequestIntegrationTest extends AbstractProcessorDatabaseIntegrationTests {

	public void testWithNoError() throws Exception {
		String email = "someEmail";

		String salt1 = makeSalt();
		signup(email, salt1, "hash");
		
		String sessionSalt = makeSalt();
		String emailSalt = requestEmailSalt( sessionSalt, email);
		assertEquals(salt1, emailSalt);
	}
}
