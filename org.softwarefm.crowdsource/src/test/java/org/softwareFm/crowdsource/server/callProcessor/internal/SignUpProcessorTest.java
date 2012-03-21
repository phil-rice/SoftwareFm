/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.crowdsource.api.IIdAndSaltGenerator;
import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class SignUpProcessorTest extends AbstractProcessCallTest<SignupProcessor> {

	private final String url = "/" + LoginConstants.signupPrefix;

	private SaltProcessorMock saltProcessor;
	private SignUpCheckerMock checker;
	private final RequestLine requestLine = makeRequestLine(CommonConstants.POST, url);


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
		processor.process(requestLine, Maps.stringObjectMap(LoginConstants.sessionSaltKey, salt, LoginConstants.softwareFmIdKey, softwareFmId, LoginConstants.emailKey, "a@b.com", LoginConstants.monikerKey, "someMoniker", LoginConstants.passwordHashKey, "someHash"));
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

		IIdAndSaltGenerator generators = IIdAndSaltGenerator.Utils.mockGenerators("someSoftwareFmId{0}", null, null,null);
		return new SignupProcessor(checker, saltProcessor, generators);
	}

	private Map<String, Object> makeData(String salt) {
		return Maps.stringObjectMap(LoginConstants.emailKey, "a@b.com", LoginConstants.sessionSaltKey, salt, LoginConstants.passwordHashKey, "someHash", LoginConstants.monikerKey, "someMoniker");
	}

}