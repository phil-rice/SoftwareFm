package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class SignUpProcessorTest extends AbstractProcessCallTest<SignupProcessor> {

	private final String url = "/" + ServerConstants.signupPrefix;

	private SaltProcessorMock saltProcessor;
	private SignUpCheckerMock checker;
	private final RequestLine requestLine = makeRequestLine(ServerConstants.POST, url);

	public void testIgnoresEverythingExceptGetWithPrefix() {
		checkIgnoresNoneGet();
		checkIgnores(ServerConstants.GET);
		assertNull(processor.process(makeRequestLine(ServerConstants.GET, url), Maps.stringObjectMap("a", 1)));
	}

	public void testInvalidatesSaltAndUsesSignupChecker() {
		String salt = saltProcessor.makeSalt();
		Map<String, Object> data = makeData(salt);
		IProcessResult result = processor.process(requestLine, data);
		checkStringResultWithMap(result, ServerConstants.emailKey, "someEmail", ServerConstants.cryptoKey, "someCrypto");

		assertEquals(salt, Lists.getOnly(checker.salts));
		assertEquals("someEmail", Lists.getOnly(checker.emails));
		assertEquals("someHash", Lists.getOnly(checker.passwordHashes));
		assertEquals(1, saltProcessor.checkAndInvalidateCount.get());
	}

	private Map<String, Object> makeData(String salt) {
		return Maps.stringObjectMap(ServerConstants.emailKey, "someEmail", ServerConstants.saltKey, salt, ServerConstants.passwordHashKey, "someHash");
	}

	public void testDoesntSIgnUpWithIllegalSalt() {
		saltProcessor.makeSalt();
		Map<String, Object> data = makeData("not a saltS");
		IProcessResult result = processor.process(requestLine, data);
		assertNotNull(result);// contents dealt with in integration test
		checkErrorResult(result, ServerConstants.notFoundStatusCode, ServerConstants.invalidSaltMessage, ServerConstants.invalidSaltMessage);
		assertEquals(1, saltProcessor.checkAndInvalidateCount.get());
		assertEquals(0, checker.salts.size());
	}

	@Override
	protected SignupProcessor makeProcessor() {
		saltProcessor = new SaltProcessorMock();
		checker = new SignUpCheckerMock(null, "someCrypto");
		return new SignupProcessor( checker, saltProcessor);
	}

}
