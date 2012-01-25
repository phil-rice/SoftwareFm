package org.softwareFm.server.processors.internal;

import java.util.concurrent.Callable;

import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.processors.AbstractProcessorIntegrationTests;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.user.IUser;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.runnable.Callables;

abstract public class AbstractProcessorMockIntegrationTests extends AbstractProcessorIntegrationTests {
	private ISoftwareFmServer server;
	protected SaltProcessorMock saltProcessor;
	protected LoginCheckerMock loginChecker;
	protected SignUpCheckerMock signUpChecker;
	protected ForgottonPasswordProcessorMock forgottonPasswordProcessor;
	protected PasswordResetterMock resetter;
	private EmailSailRequesterMock emailSaltProcessor;
	private Callable<String> softwareFmIdGenerator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		saltProcessor = new SaltProcessorMock();
		loginChecker = new LoginCheckerMock("loginCrypto", "loginCheckersSoftwareFmId");
		signUpChecker = new SignUpCheckerMock(null, "signUpCrypto");
		forgottonPasswordProcessor = new ForgottonPasswordProcessorMock(null);
		resetter = new PasswordResetterMock("theNewPassword");
		emailSaltProcessor = new EmailSailRequesterMock("someEmailHash");
		softwareFmIdGenerator = Callables.patternWithCount("someSoftwareFmId{0}");
		IUser user = IUser.Utils.noUserDetails();
		server = ISoftwareFmServer.Utils.testServerPort(IProcessCall.Utils.chain(//
				new LoginProcessor(saltProcessor, loginChecker), //
				new SignupProcessor(signUpChecker, saltProcessor, softwareFmIdGenerator, user), //
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
