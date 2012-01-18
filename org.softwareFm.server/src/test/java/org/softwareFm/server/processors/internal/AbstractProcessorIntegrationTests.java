package org.softwareFm.server.processors.internal;

import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.httpClient.api.IClientBuilder;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.tests.IIntegrationTest;

abstract public class AbstractProcessorIntegrationTests extends TestCase implements IIntegrationTest {
	protected IClientBuilder client;

	protected String signup(String email, String salt, String hash) {
		MapCallback callback = new MapCallback();
		signup(email, salt, hash, callback);
		Map<String, Object> map = callback.map;
		assertEquals(2, map.size());
		assertEquals(email, map.get(ServerConstants.emailKey));
		return (String) map.get(ServerConstants.cryptoKey);
	}

	protected void signup(String email, String salt, String hash, IResponseCallback callback) {
		try {
			client.post(ServerConstants.signupPrefix).//
					addParam(ServerConstants.emailKey, email).//
					addParam(ServerConstants.saltKey, salt).//
					addParam(ServerConstants.passwordHashKey, hash).//
					execute(callback).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void resetPassword(String magicString, IResponseCallback callback) {
		try {
			client.get(ServerConstants.passwordResetLinkPrefix + "/" + magicString).//
					addParam(ServerConstants.emailKey, "someEmail").//
					execute(callback).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void forgotPassword(String email, String salt) {
		MapCallback callback = new MapCallback();
		forgotPassword(email, salt, callback);
		assertEquals(email, callback.map.get(ServerConstants.emailKey));
		
		
	}
	protected void forgotPassword(String email, String salt, IResponseCallback callback) {
		try {
			client.post(ServerConstants.forgottonPasswordPrefix).//
					addParam(ServerConstants.emailKey, email).//
					addParam(ServerConstants.saltKey, salt).//
					execute(callback).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected String makeSalt() {
		try {
			StringCallback callback = new StringCallback();
			client.get(ServerConstants.makeSaltPrefix).execute(callback).get(); // salt won't be used but we want it removed
			return callback.string;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void login(String email, String salt, String hash, IResponseCallback callback) {
		try {
			client.post(ServerConstants.loginCommandPrefix).//
					addParam(ServerConstants.emailKey, email).//
					addParam(ServerConstants.saltKey, salt).//
					addParam(ServerConstants.passwordHashKey, hash).//
					execute(callback).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		client = IHttpClient.Utils.builder("localhost", ServerConstants.testPort);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		client.shutdown();
	}

	static class StringCallback implements IResponseCallback {

		public String string;

		@Override
		public void process(IResponse response) {
			assertEquals(ServerConstants.okStatusCode, response.statusCode());
			string = response.asString();

		}

	}

	static class MapCallback implements IResponseCallback {

		public Map<String, Object> map;

		@Override
		public void process(IResponse response) {
			assertEquals(ServerConstants.okStatusCode, response.statusCode());
			map = Json.mapFromString(response.asString());

		}

	}
}
