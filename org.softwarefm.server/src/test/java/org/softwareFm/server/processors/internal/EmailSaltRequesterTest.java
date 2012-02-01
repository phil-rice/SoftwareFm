package org.softwareFm.server.processors.internal;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.utilities.tests.IIntegrationTest;
import org.springframework.jdbc.core.JdbcTemplate;

public class EmailSaltRequesterTest extends TestCase implements IIntegrationTest {

	private EmailSaltRequester saltRequester;
	private BasicDataSource dataSource;
	private JdbcTemplate template;

	public void testWhenEmailPresent() {
		template.update("insert into users (email, salt) values (?,?)", "email1", "salt1");
		template.update("insert into users (email, salt) values (?,?)", "email2", "salt2");
		assertEquals("salt1", saltRequester.getSalt("email1"));
		assertEquals("salt2", saltRequester.getSalt("email2"));
	}

	public void testWhenEmailNotPresent() {
		assertNull(saltRequester.getSalt("email1"));
	}

	@Override
	protected void setUp() throws Exception {
		dataSource = AbstractLoginDataAccessor.defaultDataSource();
		saltRequester = new EmailSaltRequester(dataSource);
		template = new JdbcTemplate(dataSource);
		template.update("truncate users");
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dataSource.close();
	}

}
