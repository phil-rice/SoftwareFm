/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
import org.softwareFm.common.tests.Tests;
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
		assertEquals("a@b.com", Lists.getOnly(checker.emails));
		assertEquals("someHash", Lists.getOnly(checker.passwordHashes));
		assertEquals("someMoniker", Lists.getOnly(checker.monikers));
		assertEquals(1, saltProcessor.checkAndInvalidateCount.get());
	}

	public void testCreatesUserDetails() {
		String softwareFmId = "someSoftwareFmId0";
		String salt = saltProcessor.makeSalt();
		EasyMock.replay(user);// no calls to user

		processor.process(requestLine, Maps.stringObjectMap(LoginConstants.sessionSaltKey, salt, LoginConstants.softwareFmIdKey, softwareFmId, LoginConstants.emailKey, "a@b.com", LoginConstants.monikerKey, "someMoniker", LoginConstants.passwordHashKey, "someHash"));

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

	public void testThrowsExceptionIfParametersInvalid() {
		checkThrowsException(null, "salt", "moniker", "passwordHash", "email, {moniker=moniker, passwordHash=passwordHash, sessionSalt=salt}");
		checkThrowsException("a@b.com", null, "moniker", "passwordHash", "sessionSalt, {email=a@b.com, moniker=moniker, passwordHash=passwordHash}");
		checkThrowsException("a@b.com", "salt", null, "passwordHash", "moniker, {email=a@b.com, passwordHash=passwordHash, sessionSalt=salt}");
		checkThrowsException("a@b.com", "salt", "moniker", null, "passwordHash, {email=a@b.com, moniker=moniker, sessionSalt=salt}");

		checkThrowsException("invalidEmail", "salt", "moniker", "passwordHash", "Invalid email invalidEmail");
	}

	private void checkThrowsException(String email, String salt, String moniker, String hash, String expectedMessaget) {
		final Map<String, Object> parameters = Maps.makeMapWithoutNullValues(LoginConstants.emailKey, email, LoginConstants.sessionSaltKey, salt, LoginConstants.monikerKey, moniker, LoginConstants.passwordHashKey, hash);
		Tests.assertThrowsWithMessage(expectedMessaget, IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				processor.process(requestLine, parameters);
			}
		});

	}

	@Override
	protected SignupProcessor makeProcessor() {
		saltProcessor = new SaltProcessorMock();

		checker = new SignUpCheckerMock(null, "someCrypto");

		user = EasyMock.createMock(IUser.class);
		return new SignupProcessor(checker, saltProcessor, Callables.patternWithCount("someSoftwareFmId{0}"));
	}

	private Map<String, Object> makeData(String salt) {
		return Maps.stringObjectMap(LoginConstants.emailKey, "a@b.com", LoginConstants.sessionSaltKey, salt, LoginConstants.passwordHashKey, "someHash", LoginConstants.monikerKey, "someMoniker");
	}

}