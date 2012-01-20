package org.softwareFm.server.processors.internal;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.processors.AbstractLoginDataAccessor;
import org.softwareFm.server.processors.IPasswordResetter;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.utilities.callbacks.ICallback;
import org.springframework.jdbc.core.JdbcTemplate;

abstract public class AbstractProcessorDatabaseIntegrationTests extends AbstractProcessorIntegrationTests {
	private ISoftwareFmServer server;
	protected JdbcTemplate template;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		SaltProcessor saltProcessor = new SaltProcessor();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		LoginChecker loginChecker = new LoginChecker(dataSource);
		SignUpChecker signUpChecker = new SignUpChecker(dataSource);
		ForgottonPasswordMailer forgottonPasswordProcessor = new ForgottonPasswordMailer(dataSource, null, null, null);
		IPasswordResetter resetter = new PasswordResetter(dataSource);
		EmailSaltRequester saltRequester = new EmailSaltRequester(dataSource);
		IProcessCall result = IProcessCall.Utils.chain(//
				new LoginProcessor(saltProcessor, loginChecker), //
				new SignupProcessor(signUpChecker, saltProcessor), //
				new MakeSaltForLoginProcessor(saltProcessor),//
				new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordProcessor),//
				new RequestEmailSaltProcessor(saltRequester),//
				new ForgottonPasswordWebPageProcessor(resetter));
		template = new JdbcTemplate(dataSource);
		server = ISoftwareFmServer.Utils.testServerPort(result, ICallback.Utils.rethrow());
		template.update("truncate users");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
	}

	protected String getPasswordResetKeyFor(String email) {
		return template.queryForObject("select passwordResetKey from users where email=?", String.class, email);
	}
}
