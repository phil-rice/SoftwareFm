package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class LoginProcessorTest extends AbstractProcessCallTest<LoginProcessor> {

	private final String url = "/" + ServerConstants.loginCommandPrefix;

	private final SaltProcessorMock saltProcessor = new SaltProcessorMock();
	private final RequestLine requestLine = makeRequestLine(ServerConstants.POST, url);
	private final LoginCheckerMock checker = new LoginCheckerMock("someCrypto");
	private final Map<String, Object> data = Maps.stringObjectMap(ServerConstants.emailKey, "someEmail", ServerConstants.passwordHashKey, "someHash");

	public void testIgnoresEverythingExceptGetWithPrefix() {
		checkIgnoresNoneGet();
		checkIgnores(ServerConstants.GET);
		assertNull(processor.process(makeRequestLine(ServerConstants.GET, url), data));
		assertEquals(0, checker.emails.size());
	}

	public void testLogsIn() {
		IProcessResult processResult = processor.process(requestLine, data);
		checkStringResultWithMap(processResult, ServerConstants.cryptoKey, "someCrypto", ServerConstants.emailKey, "someEmail");
		assertEquals("someEmail", Lists.getOnly(checker.emails));
		assertEquals("someHash", Lists.getOnly(checker.passwordHashes));
	}
	
	public void testDoesntLogInIfCheckerRespondsBadly(){
		checker.setCrypto(null);
		IProcessResult processResult = processor.process(requestLine, data);
		checkErrorResult(processResult, ServerConstants.notFoundStatusCode, ServerConstants.emailPasswordMismatch,ServerConstants.emailPasswordMismatch);
		assertEquals("someEmail", Lists.getOnly(checker.emails));
		assertEquals("someHash", Lists.getOnly(checker.passwordHashes));
		
	}

	@Override
	protected LoginProcessor makeProcessor() {
		return new LoginProcessor( saltProcessor, checker);
	}

}
