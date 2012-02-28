package org.softwareFm.server.processors.internal;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.springframework.jdbc.core.JdbcTemplate;

public class MagicStringForPasswordTest extends TestCase {

	private MagicStringForPassword magicStringForPassword;
	private JdbcTemplate template;
	private final String email = "someEmail";

	public void testGeneratesNewPasswordResetKeyWhenNoneSet() {
		template.update("insert into users(email) values(?)", email);
		assertEquals("string1", magicStringForPassword.allowResetPassword(email));
		assertEquals("string1", magicStringForPassword.allowResetPassword(email));
	}

	public void testReusesNewPasswordResetKeyWhenSet() {
		template.update("insert into users(email,passwordResetKey) values(?,?)", email, "thisValue");
		assertEquals("thisValue", magicStringForPassword.allowResetPassword(email));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		template = new JdbcTemplate(dataSource);
		magicStringForPassword = new MagicStringForPassword(dataSource, Callables.valueFromList("string1", "string2"));
		template.update("delete from users");
	}
}
