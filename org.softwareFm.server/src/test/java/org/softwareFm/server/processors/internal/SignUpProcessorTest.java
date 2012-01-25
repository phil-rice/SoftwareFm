package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.server.user.IUser;
import org.softwareFm.server.user.internal.UserMock;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.runnable.Callables;

public class SignUpProcessorTest extends AbstractProcessCallTest<SignupProcessor> {

	private final String url = "/" + ServerConstants.signupPrefix;

	private SaltProcessorMock saltProcessor;
	private SignUpCheckerMock checker;
	private final RequestLine requestLine = makeRequestLine(ServerConstants.POST, url);

	private UserMock userMock;

	public void testIgnoresEverythingExceptGetWithPrefix() {
		checkIgnoresNoneGet();
		checkIgnores(ServerConstants.GET);
		assertNull(processor.process(makeRequestLine(ServerConstants.GET, url), Maps.stringObjectMap("a", 1)));
	}

	public void testInvalidatesSaltAndUsesSignupChecker() {
		String salt = saltProcessor.makeSalt();
		Map<String, Object> data = makeData(salt);
		IProcessResult result = processor.process(requestLine, data);
		checkStringResultWithMap(result, ServerConstants.cryptoKey, "someCrypto", ServerConstants.softwareFmIdKey, "someSoftwareFmId0");

		assertEquals(salt, Lists.getOnly(checker.salts));
		assertEquals("someEmail", Lists.getOnly(checker.emails));
		assertEquals("someHash", Lists.getOnly(checker.passwordHashes));
		assertEquals(1, saltProcessor.checkAndInvalidateCount.get());
	}

	public void testCreatesUserDetails() {
		String salt = saltProcessor.makeSalt();
		processor.process(requestLine, Maps.stringObjectMap(ServerConstants.sessionSaltKey, salt, ServerConstants.softwareFmIdKey, "someSoftwareFmId0", ServerConstants.emailKey, "someEmail"));
		assertEquals(Maps.makeMap(ServerConstants.cryptoKey, "someCrypto", ServerConstants.softwareFmIdKey, "someSoftwareFmId0"), Lists.getOnly(userMock.userDetailMaps));
		assertEquals(Maps.makeMap(ServerConstants.emailKey, "someEmail"), Lists.getOnly(userMock.datas));
		assertEquals(1, userMock.getUserDetailsCount.get());
	}

	private Map<String, Object> makeData(String salt) {
		return Maps.stringObjectMap(ServerConstants.emailKey, "someEmail", ServerConstants.sessionSaltKey, salt, ServerConstants.passwordHashKey, "someHash");
	}

	public void testDoesntSIgnUpWithIllegalSalt() {
		saltProcessor.makeSalt();
		Map<String, Object> data = makeData("not a salt");
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

		userMock = IUser.Utils.userMock();
		return new SignupProcessor(checker, saltProcessor, Callables.patternWithCount("someSoftwareFmId{0}"), userMock);
	}

}
