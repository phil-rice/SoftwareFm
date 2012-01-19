package org.softwareFm.collections.mySoftwareFm;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.services.IServiceExecutor;

public interface ILoginStrategy {

	void showLogin(String sessionSalt);

	void showForgotPassword(String sessionSalt);

	void showSignup(String sessionSalt);

	void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback);

	void login(String email, String sessionSalt, String password, ILoginCallback callback);

	void requestSessionSalt(IRequestSaltCallback callback);

	void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback);

	void signup(String email, String sessionSalt, String cryptoKey, String passwordHash, ISignUpCallback callback);

	public static class Utils {

		public static ILoginStrategy softwareFmLoginStrategy(final Display display, final IServiceExecutor serviceExecutor, final IHttpClient client, final IShowMessage showMessage) {
			return new ILoginStrategy() {
				@Override
				public void signup(final String email, final String sessionSalt, String cryptoKey, final String passwordHash, final ISignUpCallback callback) {
					serviceExecutor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							try {
								client.post(ServerConstants.signupPrefix).//
										addParam(ServerConstants.emailKey, email).//
										addParam(ServerConstants.saltKey, sessionSalt).//
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
				public void showSignup(String sessionSalt) {
					showMessage.showMessage(CardConstants.signupCardType, "Email sent", "Email sent");
				}

				@Override
				public void showLogin(String sessionSalt) {
					showMessage.showMessage(CardConstants.loginCardType, "Logged in", "Logged in");
				}

				@Override
				public void showForgotPassword(String sessionSalt) {
					showMessage.showMessage(CardConstants.forgotPasswordCardType, "Email sent", "Email sent");
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
				public void login(final String email, final String sessionSalt, final String password, final ILoginCallback callback) {
					serviceExecutor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							String hash = Crypto.digest(sessionSalt, password);
							client.post(ServerConstants.loginCommandPrefix).//
									addParam(ServerConstants.emailKey, email).//
									addParam(ServerConstants.saltKey, sessionSalt).//
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
							client.post(ServerConstants.forgottonPasswordPrefix).//
									addParam(ServerConstants.emailKey, email).//
									addParam(ServerConstants.saltKey, sessionSalt).//
									execute(new IResponseCallback() {
										@Override
										public void process(IResponse response) {
											display.asyncExec(new Runnable() {
												@Override
												public void run() {
													callback.emailSent(email);
												}
											});
										}
									}).get(ServerConstants.clientTimeOut, TimeUnit.SECONDS);
							return null;
						}
					});
				}

				@Override
				public void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback) {
					throw new UnsupportedOperationException();
				}
			};
		}

		public static ILoginStrategy mock(final CardConfig cardConfig, final Composite holder, final boolean ok, final ILoginCallbacks callback) {
			ILoginStrategy loginStrategy = new ILoginStrategy() {
				@Override
				public void requestSessionSalt(IRequestSaltCallback callback) {
					if (ok)
						callback.saltReceived(UUID.randomUUID().toString());
					else
						callback.problemGettingSalt("some failure message");
				}

				@Override
				public void signup(String email, String sessionSalt, String cryptoKey, String passwordHash, ISignUpCallback callback) {
					System.out.println("Signing up email: " + email + ", " + sessionSalt + " hash: " + passwordHash);
					System.out.println("Which decodes to: " + Crypto.aesDecrypt(cryptoKey, passwordHash));
					if (ok)
						callback.signedUp(email);
					else
						callback.failed(email, "some failure message");
				}

				@Override
				public void showSignup(String sessionSalt) {
					Swts.removeAllChildren(holder);
					System.out.println("Would have sent salt to SFM: " + sessionSalt);
					String crypto = Crypto.makeKey();
					System.out.println("And got reply: " + crypto);
					ISignUp.Utils.signUp(holder, cardConfig, sessionSalt, crypto, this, callback);
					holder.layout();
				}

				@Override
				public void showLogin(String sessionSalt) {
					Swts.removeAllChildren(holder);
					ILogin.Utils.login(holder, cardConfig, sessionSalt, this, callback);
					holder.layout();
				}

				@Override
				public void showForgotPassword(String sessionSalt) {
					Swts.removeAllChildren(holder);
					IForgotPassword.Utils.forgotPassword(holder, cardConfig, sessionSalt, this, callback);
					holder.layout();
				}

				@Override
				public void login(String email, String sessionSalt, String password, ILoginCallback callback) {
					System.out.println("Logging in");
					if (ok)
						callback.loggedIn(email, Crypto.makeKey());
					else
						callback.failedToLogin(email, "some failure message");
				}

				@Override
				public void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback) {
					System.out.println("Logging in");
					if (ok)
						callback.emailSent(email);
					else
						callback.failedToSend(email, "some reason");
				}

				@Override
				public void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback) {
					throw new UnsupportedOperationException();
				}

			};
			return loginStrategy;

		}

		public static ILoginStrategy sysoutLoginStrategy() {
			return new ILoginStrategy() {

				@Override
				public void showLogin(String sessionSalt) {
					System.out.println("Show login");
				}

				@Override
				public void showForgotPassword(String sessionSalt) {
					System.out.println("Show Forgot Password");
				}

				@Override
				public void showSignup(String sessionSalt) {
					System.out.println("Show Sign Up");

				}

				@Override
				public void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback) {
					System.out.println("Sending 'forgot password' to server");
					callback.emailSent(email);
				}

				@Override
				public void login(String email, String sessionSalt, String password, ILoginCallback callback) {
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
				public void signup(String email, String sessionSalt, String cryptoKey, String passwordHash, ISignUpCallback callback) {
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
	}

}
