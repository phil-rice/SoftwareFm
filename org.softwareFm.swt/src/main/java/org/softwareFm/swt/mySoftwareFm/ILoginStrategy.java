package org.softwareFm.swt.mySoftwareFm;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;
import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.response.IResponse;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.CommonMessages;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.swt.explorer.internal.UserData;

public interface ILoginStrategy {

	void requestSessionSalt(IRequestSaltCallback callback);

	void signup(String email, String moniker, String sessionSalt, String passwordHash, ISignUpCallback callback);

	void login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback);

	void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback);

	void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback);

	void changePassword(String email, String oldHash, String newHash, IChangePasswordCallback callback);

	public static class Utils {

		public static ILoginStrategy softwareFmLoginStrategy(final Display display, final IServiceExecutor serviceExecutor, final IHttpClient client) {
			if (client == null)
				throw new NullPointerException();
			return new ILoginStrategy() {
				@Override
				public void signup(final String email, final String moniker, final String sessionSalt, final String passwordHash, final ISignUpCallback callback) {
					serviceExecutor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							try {
								client.post(LoginConstants.signupPrefix).//
										addParam(LoginConstants.emailKey, email).//
										addParam(LoginConstants.monikerKey, moniker).//
										addParam(LoginConstants.sessionSaltKey, sessionSalt).//
										addParam(LoginConstants.passwordHashKey, passwordHash).//
										execute(new IResponseCallback() {
											@Override
											public void process(final IResponse response) {
												display.asyncExec(new Runnable() {
													@Override
													public void run() {
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
													}
												});
											}
										}).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
							} catch (Exception e) {
								callback.failed(email, CommonMessages.timedOut);
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
									client.get(LoginConstants.makeSaltPrefix).execute(new IResponseCallback() {
										@Override
										public void process(final IResponse response) {
											display.asyncExec(new Runnable() {
												@Override
												public void run() {
													if (response.statusCode() == CommonConstants.okStatusCode)
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
						}).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
					} catch (Exception e) {
						callback.problemGettingSalt(CommonMessages.timedOut);
					}
				}

				@Override
				public void login(final String email, final String sessionSalt, final String emailSalt, final String password, final ILoginCallback callback) {
					serviceExecutor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							String hash = Crypto.digest(emailSalt, password);
							client.post(LoginConstants.loginCommandPrefix).//
									addParam(LoginConstants.emailKey, email).//
									addParam(LoginConstants.sessionSaltKey, sessionSalt).//
									addParam(LoginConstants.passwordHashKey, hash).//
									execute(new IResponseCallback() {
										@Override
										public void process(final IResponse response) {
											display.asyncExec(new Runnable() {
												@Override
												public void run() {
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
												}
											});
										}
									}).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
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
								client.post(LoginConstants.forgottonPasswordPrefix).//
										addParam(LoginConstants.emailKey, email).//
										addParam(LoginConstants.sessionSaltKey, sessionSalt).//
										execute(new IResponseCallback() {
											@Override
											public void process(final IResponse response) {
												display.asyncExec(new Runnable() {
													@Override
													public void run() {
														if (response.statusCode() == CommonConstants.okStatusCode)
															callback.emailSent(email);
														else
															callback.failedToSend(email, response.asString());
													}
												});
											}
										}).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
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
							client.post(CommonConstants.emailSaltPrefix).//
									addParam(LoginConstants.sessionSaltKey, sessionSalt).//
									addParam(LoginConstants.emailKey, email).//
									execute(new IResponseCallback() {
										@Override
										public void process(final IResponse response) {
											display.asyncExec(new Runnable() {
												@Override
												public void run() {
													if (response.statusCode() == CommonConstants.okStatusCode)
														callback.saltReceived(response.asString());
													else
														callback.problemGettingSalt(response.asString());
												}
											});
										}
									}).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
							return null;
						}
					});
				}

				@Override
				public void changePassword(final String email, final String oldHash, final String newHash, final IChangePasswordCallback callback) {
					serviceExecutor.submit(new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							client.post(LoginConstants.changePasswordPrefix).//
									addParam(LoginConstants.emailKey, email).//
									addParam(LoginConstants.passwordHashKey, oldHash).//
									addParam(LoginConstants.newPasswordHashKey, newHash).//
									execute(new IResponseCallback() {
										@Override
										public void process(final IResponse response) {
											display.asyncExec(new Runnable() {
												@Override
												public void run() {
													if (response.statusCode() == CommonConstants.okStatusCode)
														callback.changedPassword(email);
													else
														callback.failedToChangePassword(email, response.asString());
												}
											});
										}
									}).get(CommonConstants.testTimeOutMs, TimeUnit.MILLISECONDS);
							return null;
						}
					});
				}

			};
		}

		public static ILoginStrategy sysoutLoginStrategy() {
			return new ILoginStrategy() {
				Callable<String> softwareFmIdGenerator = Callables.patternWithCount("softwareFmid{0}");

				@Override
				public void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback) {
					System.out.println("Sending 'forgot password' to server");
					callback.emailSent(email);
				}

				@Override
				public void login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback) {
					String key = Crypto.makeKey();
					System.out.println("Sending 'login' to server, would have received key: " + key);
					callback.loggedIn(new UserData(email, Callables.call(softwareFmIdGenerator), key));
				}

				@Override
				public void requestSessionSalt(IRequestSaltCallback callback) {
					System.out.println("Requesting salt");
					callback.saltReceived(UUID.randomUUID().toString());
				}

				@Override
				public void signup(String email, String moniker, String sessionSalt, String passwordHash, ISignUpCallback callback) {
					System.out.println("Signing up: " + email + ", " + moniker + ", " + sessionSalt + ", " + passwordHash);
					callback.signedUp(new UserData(email, Callables.call(softwareFmIdGenerator), Crypto.makeKey()));
				}

				@Override
				public void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback) {
					System.out.println("requesting email salt: " + email + ", " + sessionSalt);
					callback.saltReceived(UUID.randomUUID().toString());
				}

				@Override
				public void changePassword(String email, String oldHash, String newHash, IChangePasswordCallback callback) {
					System.out.println("changePassword: " + email + ", " + oldHash + ", " + newHash);
					callback.changedPassword(email);

				}

			};

		}

		public static ILoginStrategy noLoginStrategy() {
			return new ILoginStrategy() {

				@Override
				public void signup(String email, String moniker, String sessionSalt, String passwordHash, ISignUpCallback callback) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void requestSessionSalt(IRequestSaltCallback callback) {
					callback.problemGettingSalt("Unsupported");
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

				@Override
				public void changePassword(String email, String oldHash, String newHash, IChangePasswordCallback callback) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

}
