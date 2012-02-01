package org.softwareFm.server.processors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.tests.IIntegrationTest;
import org.softwareFm.common.tests.Tests;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.server.processors.internal.ForgottonPasswordMailer;
import org.softwareFm.server.processors.internal.LoginChecker;
import org.softwareFm.server.processors.internal.MailerMock;
import org.softwareFm.server.processors.internal.PasswordResetter;
import org.softwareFm.server.processors.internal.SignUpChecker;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

abstract public class AbstractLoginSignupForgotCheckerTest extends TestCase implements IIntegrationTest {

	protected SignUpChecker signupChecker;
	protected LoginChecker loginChecker;
	protected ForgottonPasswordMailer passwordMailer;
	protected PasswordResetter resetPassword;
	private BasicDataSource dataSource;
	protected JdbcTemplate template;
	private MailerMock mailerMock;
	private String key;

	protected String checkSignup(final String email, final String salt, final String hash, final String softwareFmId) {
		int initial = findUsersSize();
		SignUpResult signUp = signupChecker.signUp(email, salt, hash, softwareFmId);
		assertNull(signUp.errorMessage);
		final String crypto = signUp.crypto;
		final AtomicInteger count = new AtomicInteger();
		template.query("select * from users where email = ?", new Object[] { email }, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				assertEquals(email, rs.getString("email"));
				assertEquals(salt, rs.getString("salt"));
				assertEquals(hash, rs.getString("password"));
				assertEquals(crypto, rs.getString("crypto"));
				assertEquals(softwareFmId, rs.getString("softwareFmId"));
				count.incrementAndGet();
			}
		});
		int finalCount = findUsersSize();
		assertEquals(initial + 1, finalCount);
		assertEquals(1, count.get());
		return crypto;
	}

	protected int findUsersSize() {
		return template.queryForInt("select count(*) from users");
	}

	protected void checkNotAdded(final String email, final String salt, final String hash, final String softwareFmId) {
		int initial = findUsersSize();
		SignUpResult signUp = signupChecker.signUp(email, salt, hash, softwareFmId);
		assertNull(signUp.crypto);
		assertEquals(MessageFormat.format(LoginMessages.existingEmailAddress, email), signUp.errorMessage);
		int finalCount = findUsersSize();
		assertEquals(initial, finalCount);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataSource = AbstractLoginDataAccessor.defaultDataSource();
		key = Crypto.makeKey();
		signupChecker = new SignUpChecker(dataSource, Callables.value(key));
		loginChecker = new LoginChecker(dataSource);
		mailerMock = new MailerMock();
		passwordMailer = new ForgottonPasswordMailer(dataSource, mailerMock);// bit of a cheat...won't actually mail
		resetPassword = new PasswordResetter(dataSource);
		template = new JdbcTemplate(dataSource);
		template.update("truncate users");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dataSource.close();
	}

	protected void checkLogin(String email, String hash, String crypto, String expectedSfmId) {
		int count = findUsersSize();

		Map<String, String> map = loginChecker.login(email, hash);

		assertEquals(crypto, map.get(LoginConstants.cryptoKey));
		assertEquals(expectedSfmId, map.get(LoginConstants.softwareFmIdKey));
		assertEquals(count, findUsersSize());
	}

	protected void checkCannotLogin(String email, String hash) {
		int count = findUsersSize();
		Map<String, String> map = loginChecker.login(email, hash);
		assertNull(map);
		assertEquals(count, findUsersSize());
	}

	protected String checkSendPasswordEmail(final String email) {
		int initial = findUsersSize();
		String magicString = passwordMailer.process(email);
		final AtomicInteger count = new AtomicInteger();
		template.query("select * from users where passwordResetKey = ?", new Object[] { magicString }, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				assertEquals(email, rs.getString("email"));
				count.incrementAndGet();
			}
		});
		int finalCount = findUsersSize();
		assertEquals(initial, finalCount);
		assertEquals(1, count.get());
		return magicString;
	}

	protected void checkCannotSendEMailPassword(final String email) {
		int initialCount = findUsersSize();
		Tests.assertThrows(RuntimeException.class, new Runnable() {
			@Override
			public void run() {
				passwordMailer.process(email);
			}
		});

		assertEquals(0, template.queryForInt("select count(*) from users where email = ?", email));
		int finalCount = findUsersSize();
		assertEquals(initialCount, finalCount);
	}

}
