package org.softwareFm.server.processors.internal;

import junit.framework.TestCase;

import org.softwareFm.httpClient.api.IClientBuilder;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.tests.IIntegrationTest;

public class FullLoginIntegrationTests extends TestCase implements IIntegrationTest {
	private IClientBuilder client;
	private ISoftwareFmServer server;
	private SaltProcessor saltProcessor;
	private LoginChecker loginChecker;
	private SignUpChecker signUpChecker;
	private ForgottonPasswordMailer forgottonPasswordProcessor;
	private PasswordResetterMock resetter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		saltProcessor = new SaltProcessor();
		loginChecker = new LoginChecker();
		signUpChecker = new SignUpChecker();
		forgottonPasswordProcessor = new ForgottonPasswordMailer(null, null, null);
		resetter = new PasswordResetterMock("theNewPassword");
		server = ISoftwareFmServer.Utils.testServerPort(IProcessCall.Utils.chain(//
				new LoginProcessor(saltProcessor, loginChecker), //
				new SignupProcessor(signUpChecker, saltProcessor), //
				new MakeSaltForLoginProcessor(saltProcessor),//
				new ForgottonPasswordProcessor(saltProcessor, forgottonPasswordProcessor),//
				new ForgottonPasswordWebPageProcessor(resetter)), ICallback.Utils.rethrow());
		client = IHttpClient.Utils.builder("localhost", ServerConstants.testPort);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
		client.shutdown();
	}
}
