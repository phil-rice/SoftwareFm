package org.softwareFm.server.processors.internal;

import junit.framework.TestCase;

import org.softwareFm.httpClient.api.IClientBuilder;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.ISignUpChecker;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.tests.IIntegrationTest;

abstract public class AbstractProcessorIntegrationTests extends TestCase implements IIntegrationTest {
	private ISoftwareFmServer server;
	protected ISignUpChecker signUpChecker;
	protected LoginCheckerMock loginChecker;
	protected SaltProcessorMock saltProcessor;
	protected IClientBuilder client;
	protected ForgottonPasswordProcessorMock forgottonPasswordProcessor;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		saltProcessor = new SaltProcessorMock();
		loginChecker = new LoginCheckerMock("loginCrypto");
		signUpChecker = new SignUpCheckerMock(null, "signUpCrypto");
		forgottonPasswordProcessor = new ForgottonPasswordProcessorMock(null);
		server = ISoftwareFmServer.Utils.testServerPort(IProcessCall.Utils.chain(//
				new LoginProcessor(saltProcessor, loginChecker), //
				new SignupProcessor(signUpChecker, saltProcessor), //
				new MakeSaltForLoginProcessor(saltProcessor),//
				new ForgottonPasswordProcessor( saltProcessor, forgottonPasswordProcessor)), ICallback.Utils.rethrow());
		client = IHttpClient.Utils.builder("localhost", ServerConstants.testPort);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
		client.shutdown();
	}
}
