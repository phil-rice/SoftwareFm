package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.server.processors.IProcessResult;

public class LoginProcessorTest extends AbstractProcessCallTest<LoginProcessor> {

	private final String url = "/" + LoginConstants.loginCommandPrefix;

	private final SaltProcessorMock saltProcessor = new SaltProcessorMock();
	private final RequestLine requestLine = makeRequestLine(CommonConstants.POST, url);
	private final LoginCheckerMock checker = new LoginCheckerMock("someCrypto", "checkSoftwareFmId");
	private final Map<String, Object> data = Maps.stringObjectMap(LoginConstants.emailKey, "someEmail", LoginConstants.passwordHashKey, "someHash");

	public void testIgnoresEverythingExceptGetWithPrefix() {
		checkIgnoresNoneGet();
		checkIgnores(CommonConstants.GET);
		assertNull(processor.process(makeRequestLine(CommonConstants.GET, url), data));
		assertEquals(0, checker.emails.size());
	}

	public void testLogsIn() {
		IProcessResult processResult = processor.process(requestLine, data);
		checkStringResultWithMap(processResult, LoginConstants.cryptoKey, "someCrypto", LoginConstants.emailKey, "someEmail", LoginConstants.softwareFmIdKey, "checkSoftwareFmId");
		assertEquals("someEmail", Lists.getOnly(checker.emails));
		assertEquals("someHash", Lists.getOnly(checker.passwordHashes));
	}
	
	public void testDoesntLogInIfCheckerRespondsBadly(){
		checker.setResultToNull();
		IProcessResult processResult = processor.process(requestLine, data);
		checkErrorResult(processResult, CommonConstants.notFoundStatusCode, LoginMessages.emailPasswordMismatch,LoginMessages.emailPasswordMismatch);
		assertEquals("someEmail", Lists.getOnly(checker.emails));
		assertEquals("someHash", Lists.getOnly(checker.passwordHashes));
		
	}

	@Override
	protected LoginProcessor makeProcessor() {
		return new LoginProcessor( saltProcessor, checker);
	}

}
