/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.ICrowdSourcesApi;
import org.softwareFm.crowdsource.api.ICryptoGenerators;
import org.softwareFm.crowdsource.api.MailerMock;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.server.doers.internal.ForgottonPasswordMailer;
import org.softwareFm.crowdsource.server.doers.internal.LoginChecker;
import org.softwareFm.crowdsource.server.doers.internal.MagicStringForPassword;
import org.softwareFm.crowdsource.server.doers.internal.PasswordResetter;
import org.softwareFm.crowdsource.server.doers.internal.SignUpChecker;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.tests.IIntegrationTest;
import org.softwareFm.crowdsource.utilities.tests.Tests;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

abstract public class AbstractLoginSignupForgotCheckerTest extends ApiTest implements IIntegrationTest {

	protected SignUpChecker signupChecker;
	protected LoginChecker loginChecker;
	protected ForgottonPasswordMailer passwordMailer;
	protected PasswordResetter resetPassword;
	private BasicDataSource dataSource;
	protected JdbcTemplate template;
	private MailerMock mailerMock;
	private ICrowdSourcesApi api;

	protected String checkSignup(final String email, final String moniker, final String salt, final String hash, final String softwareFmId) {
		int initial = findUsersSize();
		SignUpResult signUp = signupChecker.signUp(email, moniker, salt, hash, softwareFmId);
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

		api.makeReader().accessUserReader(new IFunction1<IUserReader, Void>() {
			@Override
			public Void apply(IUserReader user) throws Exception {
				assertEquals(email, user.getUserProperty(softwareFmId, crypto, LoginConstants.emailKey));
				assertEquals(moniker, user.getUserProperty(softwareFmId, crypto, LoginConstants.monikerKey));
				return null;
			}
		});
		return crypto;
	}

	protected int findUsersSize() {
		return template.queryForInt("select count(*) from users");
	}

	protected void checkNotAdded(final String email, final String salt, final String hash, final String softwareFmId) {
		int initial = findUsersSize();
		SignUpResult signUp = signupChecker.signUp(email, "irrelevantMoniker", salt, hash, softwareFmId);
		assertNull(signUp.crypto);
		assertEquals(MessageFormat.format(LoginMessages.existingEmailAddress, email), signUp.errorMessage);
		int finalCount = findUsersSize();
		assertEquals(initial, finalCount);
	}

	@Override
	protected IMailer getMailer() {
		return mailerMock;
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataSource = AbstractLoginDataAccessor.defaultDataSource();

		ICryptoGenerators cryptoGenerators = ICryptoGenerators.Utils.mock(new String[] { Crypto.makeKey(), Crypto.makeKey() }, new String[0]);
		api = getApi();

		signupChecker = new SignUpChecker(dataSource, cryptoGenerators, api.makeReadWriter());
		loginChecker = new LoginChecker(dataSource);
		mailerMock = new MailerMock();
		IMagicStringForPassword magicStringForPassword = new MagicStringForPassword(dataSource, Callables.uuidGenerator());
		passwordMailer = new ForgottonPasswordMailer(mailerMock, magicStringForPassword);// bit of a cheat...won't actually mail
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