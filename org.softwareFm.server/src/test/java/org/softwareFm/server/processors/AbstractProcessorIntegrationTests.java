package org.softwareFm.server.processors;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.requests.MemoryResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.server.GitTest;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.tests.IIntegrationTest;

abstract public class AbstractProcessorIntegrationTests extends GitTest implements IIntegrationTest {

	
	
	protected String signup(String email, String sessionSalt, String hash, String expectedSoftwareFmId) {
		MapCallback callback = new MapCallback();
		signup(email, sessionSalt, hash, callback);
		Map<String, Object> map = callback.map;
		String crypto = (String) map.get(ServerConstants.cryptoKey);
		assertEquals(expectedSoftwareFmId, map.get(ServerConstants.softwareFmIdKey));
		assertEquals(2, map.size());
		return crypto;
	}

	protected void signup(String email, String sessionSalt, String hash, IResponseCallback callback) {
		try {
			getHttpClient().post(ServerConstants.signupPrefix).//
					addParam(ServerConstants.emailKey, email).//
					addParam(ServerConstants.sessionSaltKey, sessionSalt).//
					addParam(ServerConstants.passwordHashKey, hash).//
					execute(callback).get(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void resetPassword(String magicString, IResponseCallback callback) {
		try {
			getHttpClient().get(ServerConstants.passwordResetLinkPrefix + "/" + magicString).//
					addParam(ServerConstants.emailKey, "someEmail").//
					execute(callback).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void forgotPassword(String email, String sessionSalt) {
		MapCallback callback = new MapCallback();
		forgotPassword(email, sessionSalt, callback);
		assertEquals(email, callback.map.get(ServerConstants.emailKey));

	}

	protected void forgotPassword(String email, String sessionSalt, IResponseCallback callback) {
		try {
			getHttpClient().post(ServerConstants.forgottonPasswordPrefix).//
					addParam(ServerConstants.emailKey, email).//
					addParam(ServerConstants.sessionSaltKey, sessionSalt).//
					execute(callback).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected String makeSalt() {
		try {
			StringCallback callback = new StringCallback();
			getHttpClient().get(ServerConstants.makeSaltPrefix).execute(callback).get(); // salt won't be used but we want it removed
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
		assertEquals(email, map.get(ServerConstants.emailKey));
		assertEquals("someNewSoftwareFmId0", map.get(ServerConstants.softwareFmIdKey));
		return Strings.nullSafeToString(map.get(ServerConstants.cryptoKey));

	}

	protected void login(String email, String sessionSalt, String emailSalt, String hash, IResponseCallback callback) {
		try {
			getHttpClient().post(ServerConstants.loginCommandPrefix).//
					addParam(ServerConstants.emailKey, email).//
					addParam(ServerConstants.sessionSaltKey, sessionSalt).//
					addParam(ServerConstants.emailSaltKey, emailSalt).//
					addParam(ServerConstants.passwordHashKey, hash).//
					execute(callback).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected String requestEmailSalt(String sessionSalt, String email) {
		try {
			MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
			getHttpClient().post(ServerConstants.emailSaltPrefix).//
					addParam(ServerConstants.sessionSaltKey, sessionSalt).//
					addParam(ServerConstants.emailKey, email).//
					execute(memoryCallback).get();
			IResponse response = memoryCallback.response;
			assertEquals(memoryCallback.response.toString(), ServerConstants.okStatusCode, response.statusCode());
			return response.asString();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static class StringCallback implements IResponseCallback {

		public String string;

		@Override
		public void process(IResponse response) {
			assertEquals(ServerConstants.okStatusCode, response.statusCode());
			string = response.asString();

		}

	}

	public static class MapCallback implements IResponseCallback {

		public Map<String, Object> map;

		@Override
		public void process(IResponse response) {
			assertEquals(ServerConstants.okStatusCode, response.statusCode());
			map = Json.mapFromString(response.asString());

		}

	}
}
