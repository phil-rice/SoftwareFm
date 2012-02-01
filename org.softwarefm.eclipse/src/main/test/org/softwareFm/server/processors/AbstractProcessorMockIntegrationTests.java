    package org.softwareFm.server.processors;

import java.util.concurrent.Callable;

import org.easymock.EasyMock;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.IUser;
import org.softwareFm.server.processors.internal.EmailSailRequesterMock;
import org.softwareFm.server.processors.internal.ForgottonPasswordProcessor;
import org.softwareFm.server.processors.internal.ForgottonPasswordProcessorMock;
import org.softwareFm.server.processors.internal.ForgottonPasswordWebPageProcessor;
import org.softwareFm.server.processors.internal.LoginCheckerMock;
import org.softwareFm.server.processors.internal.LoginProcessor;
import org.softwareFm.server.processors.internal.MakeSaltForLoginProcessor;
import org.softwareFm.server.processors.internal.PasswordResetterMock;
import org.softwareFm.server.processors.internal.RequestEmailSaltProcessor;
import org.softwareFm.server.processors.internal.SaltProcessorMock;
import org.softwareFm.server.processors.internal.SignUpCheckerMock;
import org.softwareFm.server.processors.internal.SignupProcessor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.crypto.Crypto;
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
	protected IUser userMock;

	protected String signUpCrypto;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		saltProcessor = new SaltProcessorMock();
		loginChecker = new LoginCheckerMock("loginCrypto", "loginCheckersSoftwareFmId");
		signUpCrypto = Crypto.makeKey();
		signUpChecker = new SignUpCheckerMock(null, signUpCrypto);
		forgottonPasswordProcessor = new ForgottonPasswordProcessorMock(null);
		resetter = new PasswordResetterMock("theNewPassword");
		emailSaltProcessor = new EmailSailRequesterMock("someEmailHash");
		softwareFmIdGenerator = Callables.patternWithCount("someSoftwareFmId{0}");
		userMock = EasyMock.createMock(IUser.class);
		server = ISoftwareFmServer.Utils.testServerPort(IProcessCall.Utils.chain(//
				new LoginProcessor(saltProcessor, loginChecker), //
				new SignupProcessor(signUpChecker, saltProcessor, softwareFmIdGenerator, userMock), //
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
