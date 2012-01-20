package org.softwareFm.collections.mySoftwareFm;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.services.IServiceExecutor;

public interface ILoginStrategy {

	void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback);

	void login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback);

	void requestSessionSalt(IRequestSaltCallback callback);

	void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback);

	void signup(String email, String sessionSalt, String passwordHash, ISignUpCallback callback);

	public static class Utils {

		public static ILoginStrategy softwareFmLoginStrategy(final Display display, final IServiceExecutor serviceExecutor, final IHttpClient client) {
			if (client == null)
				throw new NullPointerException();
			return new ILoginStrategy() {
				@Override
				public void signup(final String email, final String sessionSalt, final String passwordHash, final ISignUpCallback callback) {
					serviceExecutor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							try {
								client.post(ServerConstants.signupPrefix).//
										addParam(ServerConstants.emailKey, email).//
										addParam(ServerConstants.sessionSaltKey, sessionSalt).//
										addParam(ServerConstants.passwordHashKey, passwordHash).//
										execute(new IResponseCallback() {
											@Override
											public void process(final IResponse response) {
												display.asyncExec(new Runnable() {
													@Override
													public void run() {
														if (response.statusCode() == ServerConstants.okStatusCode)
															callback.signedUp(email);
														else
															callback.failed(email, response.asString());
													}
												});
											}
										}).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
							} catch (Exception e) {
								callback.failed(email, ServerConstants.timedOut);
							}
							return null;
						}
					});
				}

				@Override
				public void requestSessionSalt(final IRequestSaltCallback callback) {
					try {
						serviceExecutor.submit(new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								try {
									client.get(ServerConstants.makeSaltPrefix).execute(new IResponseCallback() {
										@Override
										public void process(final IResponse response) {
											display.asyncExec(new Runnable() {
												@Override
												public void run() {
													if (response.statusCode() == ServerConstants.okStatusCode)
														callback.saltReceived(response.asString());
													else
														callback.problemGettingSalt(response.asString());
												}
											});
										}
									}); // salt won't be used but we want it removedS
								} catch (Exception e) {
									callback.problemGettingSalt(e.getMessage());
								}
								return null;
							}
						}).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
					} catch (Exception e) {
						callback.problemGettingSalt(ServerConstants.timedOut);
					}
				}

				@Override
				public void login(final String email, final String sessionSalt, final String emailSalt, final String password, final ILoginCallback callback) {
					serviceExecutor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							String hash = Crypto.digest(emailSalt, password);
							client.post(ServerConstants.loginCommandPrefix).//
									addParam(ServerConstants.emailKey, email).//
									addParam(ServerConstants.sessionSaltKey, sessionSalt).//
									addParam(ServerConstants.passwordHashKey, hash).//
									execute(new IResponseCallback() {
										@Override
										public void process(final IResponse response) {
											display.asyncExec(new Runnable() {
												@Override
												public void run() {
													if (response.statusCode() == ServerConstants.okStatusCode) {
														Map<String, Object> map = Json.mapFromString(response.asString());
														String crypto = (String) map.get(ServerConstants.cryptoKey);
														callback.loggedIn(email, crypto);
													} else
														callback.failedToLogin(email, response.asString());
												}
											});
										}
									}).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
							return null;
						}
					});
				}

				@Override
				public void forgotPassword(final String email, final String sessionSalt, final IForgotPasswordCallback callback) {
					serviceExecutor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							try {
								client.post(ServerConstants.forgottonPasswordPrefix).//
										addParam(ServerConstants.emailKey, email).//
										addParam(ServerConstants.sessionSaltKey, sessionSalt).//
										execute(new IResponseCallback() {
											@Override
											public void process(final IResponse response) {
												display.asyncExec(new Runnable() {
													@Override
													public void run() {
														if (response.statusCode() == ServerConstants.okStatusCode)
															callback.emailSent(email, response.asString());
														else
															callback.failedToSend(email, response.asString());
													}
												});
											}
										}).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
							} catch (Exception e) {
								callback.failedToSend(email, e.getMessage());
							}
							return null;
						}
					});
				}

				@Override
				public void requestEmailSalt(final String email, final String sessionSalt, final IRequestSaltCallback callback) {
					serviceExecutor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							client.post(ServerConstants.emailSaltPrefix).//
									addParam(ServerConstants.sessionSaltKey, sessionSalt).//
									addParam(ServerConstants.emailKey, email).//
									execute(new IResponseCallback() {
										@Override
										public void process(final IResponse response) {
											display.asyncExec(new Runnable() {
												@Override
												public void run() {
													if (response.statusCode() == ServerConstants.okStatusCode)
														callback.saltReceived(response.asString());
													else
														callback.problemGettingSalt(response.asString());
												}
											});
										}
									}).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
							return null;
						}
					});
				}
			};
		}

		// public static ILoginStrategy mock(final CardConfig cardConfig, final Composite holder, final boolean ok, final ILoginCallbacks callback) {
		// ILoginStrategy loginStrategy = new ILoginStrategy() {
		// @Override
		// public void requestSessionSalt(IRequestSaltCallback callback) {
		// if (ok)
		// callback.saltReceived(UUID.randomUUID().toString());
		// else
		// callback.problemGettingSalt("some failure message");
		// }
		//
		// @Override
		// public void signup(String email, String sessionSalt, String cryptoKey, String passwordHash, ISignUpCallback callback) {
		// System.out.println("Signing up email: " + email + ", " + sessionSalt + " hash: " + passwordHash);
		// System.out.println("Which decodes to: " + Crypto.aesDecrypt(cryptoKey, passwordHash));
		// if (ok)
		// callback.signedUp(email);
		// else
		// callback.failed(email, "some failure message");
		// }
		//
		// @Override
		// public void showSignup(String sessionSalt) {
		// Swts.removeAllChildren(holder);
		// System.out.println("Would have sent salt to SFM: " + sessionSalt);
		// String crypto = Crypto.makeKey();
		// System.out.println("And got reply: " + crypto);
		// ISignUp.Utils.signUp(holder, cardConfig, sessionSalt, crypto, this, callback);
		// holder.layout();
		// }
		//
		// @Override
		// public void showLogin(String sessionSalt) {
		// Swts.removeAllChildren(holder);
		// ILogin.Utils.login(holder, cardConfig, sessionSalt, this, callback);
		// holder.layout();
		// }
		//
		// @Override
		// public void showForgotPassword(String sessionSalt) {
		// Swts.removeAllChildren(holder);
		// IForgotPassword.Utils.forgotPassword(holder, cardConfig, sessionSalt, this, callback);
		// holder.layout();
		// }
		//
		// @Override
		// public void login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback) {
		// System.out.println("Logging in");
		// if (ok)
		// callback.loggedIn(email, Crypto.makeKey());
		// else
		// callback.failedToLogin(email, "some failure message");
		// }
		//
		// @Override
		// public void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback) {
		// System.out.println("Logging in");
		// if (ok)
		// callback.emailSent(email);
		// else
		// callback.failedToSend(email, "some reason");
		// }
		//
		// @Override
		// public void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback) {
		// throw new UnsupportedOperationException();
		// }
		//
		// };
		// return loginStrategy;
		//
		// }

		public static ILoginStrategy sysoutLoginStrategy() {
			return new ILoginStrategy() {

				@Override
				public void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback) {
					System.out.println("Sending 'forgot password' to server");
					callback.emailSent(email, "magicString");
				}

				@Override
				public void login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback) {
					String key = Crypto.makeKey();
					System.out.println("Sending 'login' to server, would have received key: " + key);
					callback.loggedIn(email, key);
				}

				@Override
				public void requestSessionSalt(IRequestSaltCallback callback) {
					System.out.println("Requesting salt");
					callback.saltReceived(UUID.randomUUID().toString());
				}

				@Override
				public void signup(String email, String sessionSalt, String passwordHash, ISignUpCallback callback) {
					System.out.println("Signing up: " + email + ", " + sessionSalt + ", " + passwordHash);
					callback.signedUp(email);
				}

				@Override
				public void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback) {
					System.out.println("requesting email salt: " + email + ", " + sessionSalt);
					callback.saltReceived(UUID.randomUUID().toString());
				}
			};

		}

		public static ILoginStrategy noLoginStrategy() {
			return new ILoginStrategy() {

				@Override
				public void signup(String email, String sessionSalt, String passwordHash, ISignUpCallback callback) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void requestSessionSalt(IRequestSaltCallback callback) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

}
