package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.easymock.EasyMock;
import org.softwareFm.common.IUser;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.constants.LoginMessages;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.server.processors.AbstractProcessCallTest;
import org.softwareFm.server.processors.IProcessResult;

public class SignUpProcessorTest extends AbstractProcessCallTest<SignupProcessor> {

	private final String url = "/" + LoginConstants.signupPrefix;

	private SaltProcessorMock saltProcessor;
	private SignUpCheckerMock checker;
	private final RequestLine requestLine = makeRequestLine(CommonConstants.POST, url);

	private IUser user;

	public void testIgnoresEverythingExceptGetWithPrefix() {
		checkIgnoresNoneGet();
		checkIgnores(CommonConstants.GET);
		assertNull(processor.process(makeRequestLine(CommonConstants.GET, url), Maps.stringObjectMap("a", 1)));
	}

	public void testInvalidatesSaltAndUsesSignupChecker() {
		String salt = saltProcessor.makeSalt();
		Map<String, Object> data = makeData(salt);
		IProcessResult result = processor.process(requestLine, data);
		checkStringResultWithMap(result, LoginConstants.cryptoKey, "someCrypto", LoginConstants.softwareFmIdKey, "someSoftwareFmId0");

		assertEquals(salt, Lists.getOnly(checker.salts));
		assertEquals("someEmail", Lists.getOnly(checker.emails));
		assertEquals("someHash", Lists.getOnly(checker.passwordHashes));
		assertEquals(1, saltProcessor.checkAndInvalidateCount.get());
	}

	public void testCreatesUserDetails() {
		String softwareFmId = "someSoftwareFmId0";
		String salt = saltProcessor.makeSalt();
		EasyMock.replay(user);//no calls to user

		processor.process(requestLine, Maps.stringObjectMap(LoginConstants.sessionSaltKey, salt, LoginConstants.softwareFmIdKey,softwareFmId, LoginConstants.emailKey, "someEmail", LoginConstants.monikerKey, "someMoniker"));

		EasyMock.verify(user);
	}

	public void testDoesntSIgnUpWithIllegalSalt() {
		saltProcessor.makeSalt();
		Map<String, Object> data = makeData("not a salt");
		IProcessResult result = processor.process(requestLine, data);
		assertNotNull(result);// contents dealt with in integration test
		checkErrorResult(result, CommonConstants.notFoundStatusCode, LoginMessages.invalidSaltMessage, LoginMessages.invalidSaltMessage);
		assertEquals(1, saltProcessor.checkAndInvalidateCount.get());
		assertEquals(0, checker.salts.size());
	}

	@Override
	protected SignupProcessor makeProcessor() {
		saltProcessor = new SaltProcessorMock();

		checker = new SignUpCheckerMock(null, "someCrypto");

		user = EasyMock.createMock(IUser.class);
		return new SignupProcessor(checker, saltProcessor, Callables.patternWithCount("someSoftwareFmId{0}"));
	}

	private Map<String, Object> makeData(String salt) {
		return Maps.stringObjectMap(LoginConstants.emailKey, "someEmail", LoginConstants.sessionSaltKey, salt, LoginConstants.passwordHashKey, "someHash");
	}

}
