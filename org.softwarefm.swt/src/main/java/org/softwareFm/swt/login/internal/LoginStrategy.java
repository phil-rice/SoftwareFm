/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.login.internal;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.httpClient.internal.MemoryResponseCallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.exceptions.Exceptions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.ISwtFunction1;
import org.softwareFm.swt.login.IChangePasswordCallback;
import org.softwareFm.swt.login.IForgotPasswordCallback;
import org.softwareFm.swt.login.ILoginCallback;
import org.softwareFm.swt.login.ILoginStrategy;
import org.softwareFm.swt.login.IRequestSaltCallback;
import org.softwareFm.swt.login.ISignUpCallback;

public class LoginStrategy implements ILoginStrategy {
	private final IContainer container;
	private final long timeOutMs;

	public LoginStrategy(IContainer container, long timeOutMs) {
		this.container = container;
		this.timeOutMs = timeOutMs;
	}

	@Override
	public ITransaction<Void> signup(final String email, final String moniker, final String sessionSalt, final String passwordHash, final ISignUpCallback callback) {
		return container.accessWithCallbackFn(IHttpClient.class, new IFunction1<IHttpClient, IResponse>() {
			@Override
			public IResponse apply(IHttpClient client) throws Exception {
				MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
				client.post(LoginConstants.signupPrefix).//
						addParam(LoginConstants.emailKey, email).//
						addParam(LoginConstants.monikerKey, moniker).//
						addParam(LoginConstants.sessionSaltKey, sessionSalt).//
						addParam(LoginConstants.passwordHashKey, passwordHash).//
						execute(memoryCallback).get(timeOutMs, TimeUnit.MILLISECONDS);
				return memoryCallback.response;
			}
		}, new ISwtFunction1<IResponse, Void>() {
			@Override
			public Void apply(IResponse response) throws Exception {
				String string = response.asString();
				if (response.statusCode() == CommonConstants.okStatusCode) {
					Map<String, Object> map = Json.mapFromString(string);
					String crypto = (String) map.get(LoginConstants.cryptoKey);
					String softwareFmId = (String) map.get(LoginConstants.softwareFmIdKey);
					if (crypto == null)
						throw new NullPointerException(string);
					if (softwareFmId == null)
						throw new NullPointerException(string);
					UserData userData = new UserData(email, softwareFmId, crypto);
					callback.signedUp(userData);
				} else
					callback.failed(email, string);
				return null;
			}
		});
	}

	@Override
	public ITransaction<Void> requestSessionSalt(final IRequestSaltCallback callback) {
		return container.accessWithCallbackFn(IHttpClient.class, new IFunction1<IHttpClient, IResponse>() {
			@Override
			public IResponse apply(IHttpClient client) throws Exception {
				try {
					MemoryResponseCallback memory = IResponseCallback.Utils.memoryCallback();
					client.get(LoginConstants.makeSaltPrefix).execute(memory).get(timeOutMs, TimeUnit.MILLISECONDS); // salt won't be used but we want it removedS
					return memory.response;
				} catch (Exception e) {
					callback.problemGettingSalt(Exceptions.classAndMessage(e));
					throw e;
				}
			}
		}, new ISwtFunction1<IResponse, Void>() {
			@Override
			public Void apply(IResponse response) throws Exception {
				if (response.statusCode() == CommonConstants.okStatusCode)
					callback.saltReceived(response.asString());
				else
					callback.problemGettingSalt(response.asString());
				return null;
			}
		});
	}

	@Override
	public ITransaction<Void> login(final String email, final String sessionSalt, final String emailSalt, final String password, final ILoginCallback callback) {
		return container.accessWithCallbackFn(IHttpClient.class, new IFunction1<IHttpClient, IResponse>() {
			@Override
			public IResponse apply(IHttpClient client) throws Exception {
				try {
					String hash = Crypto.digest(emailSalt, password);
					MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
					client.post(LoginConstants.loginCommandPrefix).//
							addParam(LoginConstants.emailKey, email).//
							addParam(LoginConstants.sessionSaltKey, sessionSalt).//
							addParam(LoginConstants.passwordHashKey, hash).//
							execute(memoryCallback).get(timeOutMs, TimeUnit.MILLISECONDS);
					return memoryCallback.response;
				} catch (Exception e) {
					callback.failedToLogin(email, Exceptions.classAndMessage(e));
					throw e;
				}
			}
		}, new ISwtFunction1<IResponse, Void>() {
			@Override
			public Void apply(IResponse response) throws Exception {
				if (response.statusCode() == CommonConstants.okStatusCode) {
					Map<String, Object> map = Json.mapFromString(response.asString());
					String crypto = (String) map.get(LoginConstants.cryptoKey);
					String softwareFmId = (String) map.get(LoginConstants.softwareFmIdKey);
					if (crypto == null || softwareFmId == null)
						throw new NullPointerException(map.toString());
					UserData userData = new UserData(email, softwareFmId, crypto);
					callback.loggedIn(userData);
				} else
					callback.failedToLogin(email, response.asString());
				return null;
			}
		});
	}

	@Override
	public ITransaction<Void> forgotPassword(final String email, final String sessionSalt, final IForgotPasswordCallback callback) {
		return container.accessWithCallbackFn(IHttpClient.class, new IFunction1<IHttpClient, IResponse>() {
			@Override
			public IResponse apply(IHttpClient client) throws Exception {
				try {
					MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
					client.post(LoginConstants.forgottonPasswordPrefix).//
							addParam(LoginConstants.emailKey, email).//
							addParam(LoginConstants.sessionSaltKey, sessionSalt).//
							execute(memoryCallback).get(timeOutMs, TimeUnit.MILLISECONDS);
					return memoryCallback.response;
				} catch (Exception e) {
					callback.failedToSend(email, Exceptions.classAndMessage(e));
					throw e;
				}
			}
		}, new ISwtFunction1<IResponse, Void>() {
			@Override
			public Void apply(IResponse response) throws Exception {
				if (response.statusCode() == CommonConstants.okStatusCode)
					callback.emailSent(email);
				else
					callback.failedToSend(email, response.asString());
				return null;
			}
		});
	}

	@Override
	public ITransaction<Void> requestEmailSalt(final String email, final String sessionSalt, final IRequestSaltCallback callback) {
		return container.accessWithCallbackFn(IHttpClient.class, new IFunction1<IHttpClient, IResponse>() {
			@Override
			public IResponse apply(IHttpClient client) throws Exception {
				try {
					MemoryResponseCallback memory = IResponseCallback.Utils.memoryCallback();
					client.post(CommonConstants.emailSaltPrefix).//
							addParam(LoginConstants.sessionSaltKey, sessionSalt).//
							addParam(LoginConstants.emailKey, email).//
							execute(memory).get(timeOutMs, TimeUnit.MILLISECONDS);
					return memory.response;
				} catch (Exception e) {
					callback.problemGettingSalt(Exceptions.classAndMessage(e));
					throw e;
				}
			}
		}, new ISwtFunction1<IResponse, Void>() {
			@Override
			public Void apply(IResponse response) throws Exception {
				if (response.statusCode() == CommonConstants.okStatusCode)
					callback.saltReceived(response.asString());
				else
					callback.problemGettingSalt(response.asString());
				return null;
			}
		});
	}

	@Override
	public ITransaction<Void> changePassword(final String email, final String oldHash, final String newHash, final IChangePasswordCallback callback) {
		return container.accessWithCallbackFn(IHttpClient.class, new IFunction1<IHttpClient, IResponse>() {
			@Override
			public IResponse apply(IHttpClient client) throws Exception {
				try {
					MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
					client.post(LoginConstants.changePasswordPrefix).//
							addParam(LoginConstants.emailKey, email).//
							addParam(LoginConstants.passwordHashKey, oldHash).//
							addParam(LoginConstants.newPasswordHashKey, newHash).//
							execute(memoryCallback).get(timeOutMs, TimeUnit.MILLISECONDS);
					return memoryCallback.response;
				} catch (Exception e) {
					callback.failedToChangePassword(email, Exceptions.classAndMessage(e));
					throw e;
				}
			}
		}, new ISwtFunction1<IResponse, Void>() {
			@Override
			public Void apply(IResponse response) throws Exception {
				if (response.statusCode() == CommonConstants.okStatusCode)
					callback.changedPassword(email);
				else
					callback.failedToChangePassword(email, response.asString());
				return null;
			}
		});
	}
}