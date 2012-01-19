package org.softwareFm.server.processors.internal;

import org.softwareFm.server.ISoftwareFmServer;
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
		LoginChecker loginChecker = new LoginChecker();
		SignUpChecker signUpChecker = new SignUpChecker();
		ForgottonPasswordMailer forgottonPasswordProcessor = new ForgottonPasswordMailer(null, null, null);
		IPasswordResetter resetter = new PasswordResetter();
		EmailSaltRequester saltRequester = new EmailSaltRequester();
		server = ISoftwareFmServer.Utils.testServerPort(IProcessCall.Utils.chain(//
				new LoginProcessor(saltProcessor, loginChecker), //
				new SignupProcessor(signUpChecker, saltProcessor), //
				new MakeSaltForLoginProcessor(saltProcessor),//
				new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordProcessor),//
				new RequestEmailSaltProcessor(saltRequester),//
				new ForgottonPasswordWebPageProcessor(resetter)), ICallback.Utils.rethrow());
		template = signUpChecker.template;
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
