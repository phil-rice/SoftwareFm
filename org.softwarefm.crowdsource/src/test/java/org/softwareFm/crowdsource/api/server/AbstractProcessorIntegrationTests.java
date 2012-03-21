/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.server;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.httpClient.internal.MemoryResponseCallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.tests.IIntegrationTest;

abstract public class AbstractProcessorIntegrationTests extends ApiTest implements IIntegrationTest {

	protected String signup(String email, String sessionSalt, String moniker, String hash, String expectedSoftwareFmId) {
		MapCallback callback = new MapCallback();
		signup(email, sessionSalt, moniker, hash, callback);
		Map<String, Object> map = callback.map;
		String crypto = (String) map.get(LoginConstants.cryptoKey);
		assertEquals(expectedSoftwareFmId, map.get(LoginConstants.softwareFmIdKey));
		assertEquals(2, map.size());
		return crypto;
	}

	protected void signup(String email, String sessionSalt, String moniker, String hash, IResponseCallback callback) {
		try {
			getHttpClient().post(LoginConstants.signupPrefix).//
					addParam(LoginConstants.emailKey, email).//
					addParam(LoginConstants.sessionSaltKey, sessionSalt).//
					addParam(LoginConstants.monikerKey, moniker).//
					addParam(LoginConstants.passwordHashKey, hash).//
					execute(callback).get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void resetPassword(String magicString, IResponseCallback callback) {
		try {
			getHttpClient().get(LoginConstants.passwordResetLinkPrefix + "/" + magicString).//
					addParam(LoginConstants.emailKey, "someEmail").//
					execute(callback).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void forgotPassword(String email, String sessionSalt) {
		MapCallback callback = new MapCallback();
		forgotPassword(email, sessionSalt, callback);
		assertEquals(email, callback.map.get(LoginConstants.emailKey));

	}

	protected void forgotPassword(String email, String sessionSalt, IResponseCallback callback) {
		try {
			getHttpClient().post(LoginConstants.forgottonPasswordPrefix).//
					addParam(LoginConstants.emailKey, email).//
					addParam(LoginConstants.sessionSaltKey, sessionSalt).//
					execute(callback).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected String makeSalt() {
		try {
			StringCallback callback = new StringCallback();
			getHttpClient().get(LoginConstants.makeSaltPrefix).execute(callback).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS); // salt won't be used but we want it removed
			return callback.string;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected String login(String email, String sessionSalt, String emailSalt, String hash) {
		MapCallback callback = new MapCallback();
		login(email, sessionSalt, emailSalt, hash, callback);
		Map<String, Object> map = callback.map;
		assertEquals(3, map.size());
		assertEquals(email, map.get(LoginConstants.emailKey));
		assertEquals("someNewSoftwareFmId0", map.get(LoginConstants.softwareFmIdKey));
		return Strings.nullSafeToString(map.get(LoginConstants.cryptoKey));

	}

	protected void login(String email, String sessionSalt, String emailSalt, String hash, IResponseCallback callback) {
		try {
			getHttpClient().post(LoginConstants.loginCommandPrefix).//
					addParam(LoginConstants.emailKey, email).//
					addParam(LoginConstants.sessionSaltKey, sessionSalt).//
					addParam(LoginConstants.emailSaltKey, emailSalt).//
					addParam(LoginConstants.passwordHashKey, hash).//
					execute(callback).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected String requestEmailSalt(String sessionSalt, String email) {
		try {
			MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
			getHttpClient().post(CommonConstants.emailSaltPrefix).//
					addParam(LoginConstants.sessionSaltKey, sessionSalt).//
					addParam(LoginConstants.emailKey, email).//
					execute(memoryCallback).get();
			IResponse response = memoryCallback.response;
			assertEquals(memoryCallback.response.toString(), CommonConstants.okStatusCode, response.statusCode());
			return response.asString();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static class StringCallback implements IResponseCallback {

		public String string;

		@Override
		public void process(IResponse response) {
			assertEquals(CommonConstants.okStatusCode, response.statusCode());
			string = response.asString();
		}
	}

	public static class MapCallback implements IResponseCallback {

		public Map<String, Object> map;

		@Override
		public void process(IResponse response) {
			assertEquals(response.toString(), CommonConstants.okStatusCode, response.statusCode());
			map = Json.mapFromString(response.asString());

		}
	}
}