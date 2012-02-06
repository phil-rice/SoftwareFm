package org.softwareFm.server.processors.internal;

import org.softwareFm.server.processors.AbstractProcessorDatabaseIntegrationTests;


public class EmailSaltRequestIntegrationTest extends AbstractProcessorDatabaseIntegrationTests {

	public void testWithNoError() throws Exception {
		String email = "someEmail";

		String salt1 = makeSalt();
		signup(email, salt1, "someMoniker", "hash", "someNewSoftwareFmId0");
		
		String sessionSalt = makeSalt();
		String emailSalt = requestEmailSalt( sessionSalt, email);
		assertEquals(salt1, emailSalt);
	}
}
