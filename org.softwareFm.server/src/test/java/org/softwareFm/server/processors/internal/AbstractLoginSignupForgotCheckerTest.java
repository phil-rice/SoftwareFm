package org.softwareFm.server.processors.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.SignUpResult;
import org.softwareFm.utilities.tests.IIntegrationTest;
import org.softwareFm.utilities.tests.Tests;
import org.springframework.jdbc.core.RowCallbackHandler;

abstract public class AbstractLoginSignupForgotCheckerTest extends TestCase implements IIntegrationTest {

	protected SignUpChecker signupChecker;
	protected LoginChecker loginChecker;
	protected ForgottonPasswordMailer passwordMailer;
	protected PasswordResetter resetPassword;

	protected String checkSignup(final String email, final String salt, final String hash) {
		int initial = findUsersSize();
		SignUpResult signUp = signupChecker.signUp(email, salt, hash);
		assertNull(signUp.errorMessage);
		final String crypto = signUp.crypto;
		final AtomicInteger count = new AtomicInteger();
		signupChecker.template.query("select * from users where email = ?", new Object[] { email }, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				assertEquals(email, rs.getString("email"));
				assertEquals(salt, rs.getString("salt"));
				assertEquals(hash, rs.getString("password"));
				assertEquals(crypto, rs.getString("crypto"));
				count.incrementAndGet();
			}
		});
		int finalCount = findUsersSize();
		assertEquals(initial + 1, finalCount);
		assertEquals(1, count.get());
		return crypto;
	}

	protected int findUsersSize() {
		return signupChecker.template.queryForInt("select count(*) from users");
	}

	protected void checkNotAdded(final String email, final String salt, final String hash) {
		int initial = findUsersSize();
		SignUpResult signUp = signupChecker.signUp(email, salt, hash);
		assertNull(signUp.crypto);
		assertEquals(MessageFormat.format(ServerConstants.existingEmailAddress, email), signUp.errorMessage);
		int finalCount = findUsersSize();
		assertEquals(initial, finalCount);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		signupChecker = new SignUpChecker();
		loginChecker = new LoginChecker();
		passwordMailer = new ForgottonPasswordMailer(null, null, null);//bit of a cheat...won't actually mail
		resetPassword = new PasswordResetter();
		signupChecker.template.update("truncate users");
	}

	protected void checkLogin(String email, String hash, String crypto) {
		int count = findUsersSize();

		String key = loginChecker.login(email, hash);

		assertEquals(crypto, key);
		assertEquals(count, findUsersSize());
	}

	protected void checkCannotLogin(String email, String hash) {
		int count = findUsersSize();
		String key = loginChecker.login(email, hash);
		assertNull(key);
		assertEquals(count, findUsersSize());
	}

	protected String checkSendPasswordEmail(final String email) {
		int initial = findUsersSize();
		String magicString = passwordMailer.process(email);
		final AtomicInteger count = new AtomicInteger();
		signupChecker.template.query("select * from users where passwordResetKey = ?", new Object[] { magicString }, new RowCallbackHandler() {
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
	
		assertEquals(0, signupChecker.template.queryForInt("select count(*) from users where email = ?", email));
		int finalCount = findUsersSize();
		assertEquals(initialCount, finalCount);
	}

}
