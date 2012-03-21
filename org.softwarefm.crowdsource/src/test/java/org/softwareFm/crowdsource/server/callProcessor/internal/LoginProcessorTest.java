/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.tests.Tests;

public class LoginProcessorTest extends AbstractProcessCallTest<LoginProcessor> {

	private final String url = "/" + LoginConstants.loginCommandPrefix;

	private final SaltProcessorMock saltProcessor = new SaltProcessorMock();
	private final RequestLine requestLine = makeRequestLine(CommonConstants.POST, url);
	private final LoginCheckerMock checker = new LoginCheckerMock("someCrypto", "checkSoftwareFmId");
	private final Map<String, Object> data = Maps.stringObjectMap(LoginConstants.emailKey, "a@b.com", LoginConstants.passwordHashKey, "someHash", LoginConstants.sessionSaltKey, "someSalt");

	public void testIgnoresEverythingExceptGetWithPrefix() {
		checkIgnoresNoneGet();
		checkIgnores(CommonConstants.GET);
		assertNull(processor.process(makeRequestLine(CommonConstants.GET, url), data));
		assertEquals(0, checker.emails.size());
	}

	public void testLogsIn() {
		IProcessResult processResult = processor.process(requestLine, data);
		checkStringResultWithMap(processResult, LoginConstants.cryptoKey, "someCrypto", LoginConstants.emailKey, "a@b.com", LoginConstants.softwareFmIdKey, "checkSoftwareFmId");
		assertEquals("a@b.com", Lists.getOnly(checker.emails));
		assertEquals("someHash", Lists.getOnly(checker.passwordHashes));
	}

	public void testThrowsExceptionIfParametersInvalid() {
		checkThrowsException("invalidEmail", "salt", "passwordHash", "Invalid email invalidEmail");
		checkThrowsException(null, "salt", "passwordHash", "email, {passwordHash=passwordHash, sessionSalt=salt}");
		checkThrowsException("email@a.com", null, "passwordHash", "sessionSalt, {email=email@a.com, passwordHash=passwordHash}");
		checkThrowsException("email@a.com", "salt", null, "passwordHash, {email=email@a.com, sessionSalt=salt}");
	}

	private void checkThrowsException(String email, String salt, String hash, String expectedMessaget) {
		final Map<String, Object> parameters = Maps.makeMapWithoutNullValues(LoginConstants.emailKey, email, LoginConstants.sessionSaltKey, salt, LoginConstants.passwordHashKey, hash);
		Tests.assertThrowsWithMessage(expectedMessaget, IllegalArgumentException.class, new Runnable() {
			@Override
			public void run() {
				processor.process(requestLine, parameters);
			}
		});

	}

	public void testDoesntLogInIfCheckerRespondsBadly() {
		checker.setResultToNull();
		IProcessResult processResult = processor.process(requestLine, data);
		checkErrorResult(processResult, CommonConstants.notFoundStatusCode, LoginMessages.emailPasswordMismatch, LoginMessages.emailPasswordMismatch);
		assertEquals("a@b.com", Lists.getOnly(checker.emails));
		assertEquals("someHash", Lists.getOnly(checker.passwordHashes));

	}

	@Override
	protected LoginProcessor makeProcessor() {
		return new LoginProcessor(saltProcessor, checker);
	}

}