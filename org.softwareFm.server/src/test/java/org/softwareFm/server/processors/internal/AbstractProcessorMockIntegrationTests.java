package org.softwareFm.server.processors.internal;

import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.utilities.callbacks.ICallback;

abstract public class AbstractProcessorMockIntegrationTests extends AbstractProcessorIntegrationTests {
	private ISoftwareFmServer server;
	protected SaltProcessorMock saltProcessor;
	protected LoginCheckerMock loginChecker;
	protected SignUpCheckerMock signUpChecker;
	protected ForgottonPasswordProcessorMock forgottonPasswordProcessor;
	protected PasswordResetterMock resetter;
	private EmailSailRequesterMock emailSaltProcessor;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		saltProcessor = new SaltProcessorMock();
		loginChecker = new LoginCheckerMock("loginCrypto");
		signUpChecker = new SignUpCheckerMock(null, "signUpCrypto");
		forgottonPasswordProcessor = new ForgottonPasswordProcessorMock(null);
		resetter = new PasswordResetterMock("theNewPassword");
		emailSaltProcessor = new EmailSailRequesterMock("someEmailHash");
		server = ISoftwareFmServer.Utils.testServerPort(IProcessCall.Utils.chain(//
				new LoginProcessor(saltProcessor, loginChecker), //
				new SignupProcessor(signUpChecker, saltProcessor), //
				new MakeSaltForLoginProcessor(saltProcessor),//
				new RequestEmailSaltProcessor(emailSaltProcessor),//
				new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordProcessor),//
				new ForgottonPasswordWebPageProcessor(resetter)), ICallback.Utils.rethrow());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
	}

}
