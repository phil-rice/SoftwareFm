package org.softwareFm.server.processors.internal;

import junit.framework.TestCase;

import org.softwareFm.utilities.tests.IIntegrationTest;
import org.softwareFm.utilities.tests.Tests;

public class EmailSaltRequesterTest extends TestCase implements IIntegrationTest {

	private EmailSaltRequester saltRequester;

	public void testWhenEmailPresent() {
		saltRequester.template.update("insert into users (email, salt) values (?,?)", "email1", "salt1");
		saltRequester.template.update("insert into users (email, salt) values (?,?)", "email2", "salt2");
		assertEquals("salt1", saltRequester.getSalt("email1"));
		assertEquals("salt2", saltRequester.getSalt("email2"));
	}

	public void testWhenEmailNotPresent() {
		Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				saltRequester.getSalt("email1");
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		saltRequester = new EmailSaltRequester();
		saltRequester.template.update("truncate users");
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
