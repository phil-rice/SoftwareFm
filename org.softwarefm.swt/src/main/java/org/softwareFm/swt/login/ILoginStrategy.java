/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.login;

import java.text.MessageFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;
import org.softwareFm.crowdsource.api.ICrowdSourcedReadWriteApi;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.CommonMessages;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginMessages;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.exceptions.Exceptions;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.swt.swt.Swts;

public interface ILoginStrategy {

	void requestSessionSalt(IRequestSaltCallback callback);

	void signup(String email, String moniker, String sessionSalt, String passwordHash, ISignUpCallback callback);

	void login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback);

	void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback);

	void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback);

	void changePassword(String email, String oldHash, String newHash, IChangePasswordCallback callback);

	public static class Utils {

		public static ILoginStrategy softwareFmLoginStrategy(final Display display, final IServiceExecutor serviceExecutor, final ICrowdSourcedReadWriteApi readWriteApi) {
			final IHttpClient client = readWriteApi.access(IHttpClient.class, Functions.<IHttpClient, IHttpClient> identity());
			return new ILoginStrategy() {
				@Override
				public void signup(final String email, final String moniker, final String sessionSalt, final String passwordHash, final ISignUpCallback callback) {
					serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
						@Override
						@SuppressWarnings({ "finally" })
						public Void apply(IMonitor monitor) throws Exception {
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
								try {
									callback.failed(email, CommonMessages.timedOut);
								} finally {
									monitor.done();
									throw WrappedException.wrap(e);
								}
							}
							return null;
						}
					});
				}

				@Override
				public void requestSessionSalt(final IRequestSaltCallback callback) {
					try {
						serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
							@Override
							@SuppressWarnings("finally")
							public Void apply(final IMonitor monitor) throws Exception {
								monitor.beginTask(LoginMessages.requestSessionSalt, 2);
								try {
									client.get(LoginConstants.makeSaltPrefix).execute(new IResponseCallback() {
										@Override
										public void process(final IResponse response) {
											monitor.worked(1);
											Swts.asyncExecAndMarkDone(display, monitor, new Runnable() {
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
									try {
										callback.problemGettingSalt(e.getMessage());
									} finally {
										monitor.done();// note: shouldn't be done in finally, as the work continues in the swt thread
										throw WrappedException.wrap(e);
									}
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
					serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
						@SuppressWarnings("finally")
						@Override
						public Void apply(final IMonitor monitor) throws Exception {
							monitor.beginTask(MessageFormat.format(LoginMessages.loggingIn, email), 2);
							String hash = Crypto.digest(emailSalt, password);
							try {
								client.post(LoginConstants.loginCommandPrefix).//
										addParam(LoginConstants.emailKey, email).//
										addParam(LoginConstants.sessionSaltKey, sessionSalt).//
										addParam(LoginConstants.passwordHashKey, hash).//
										execute(new IResponseCallback() {
											@Override
											public void process(final IResponse response) {
												monitor.worked(1);
												Swts.asyncExecAndMarkDone(display, monitor, new Runnable() {
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
							} catch (Exception e) {
								try {
									callback.failedToLogin(email, Exceptions.classAndMessage(e));
								} finally {
									monitor.done();// note: shouldn't be done in finally, as the work continues in the swt thread
									throw WrappedException.wrap(e);
								}
							}
							return null;
						}
					});
				}

				@Override
				public void forgotPassword(final String email, final String sessionSalt, final IForgotPasswordCallback callback) {
					serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
						@Override
						public Void apply(final IMonitor monitor) throws Exception {
							try {
								monitor.beginTask(MessageFormat.format(LoginMessages.forgotPassword, email), 2);
								client.post(LoginConstants.forgottonPasswordPrefix).//
										addParam(LoginConstants.emailKey, email).//
										addParam(LoginConstants.sessionSaltKey, sessionSalt).//
										execute(new IResponseCallback() {
											@Override
											public void process(final IResponse response) {
												monitor.worked(1);
												Swts.asyncExecAndMarkDone(display, monitor, new Runnable() {
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
								monitor.done();
								throw WrappedException.wrap(e);
							}
							return null;
						}
					});
				}

				@Override
				public void requestEmailSalt(final String email, final String sessionSalt, final IRequestSaltCallback callback) {
					serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
						@Override
						public Void apply(final IMonitor monitor) throws Exception {
							monitor.beginTask(MessageFormat.format(LoginMessages.requestEmailSalt, email), 1);
							try {
								client.post(CommonConstants.emailSaltPrefix).//
										addParam(LoginConstants.sessionSaltKey, sessionSalt).//
										addParam(LoginConstants.emailKey, email).//
										execute(new IResponseCallback() {
											@Override
											public void process(final IResponse response) {
												Swts.asyncExecAndMarkDone(display, monitor, new Runnable() {
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
							} catch (Exception e) {
								callback.problemGettingSalt(Exceptions.classAndMessage(e));
								monitor.done();
								throw WrappedException.wrap(e);
							}
						}
					});
				}

				@Override
				public void changePassword(final String email, final String oldHash, final String newHash, final IChangePasswordCallback callback) {
					serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
						@Override
						@SuppressWarnings("finally")
						public Void apply(IMonitor monitor) throws Exception {
							try {
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
							} catch (Exception e) {
								try {
									callback.failedToChangePassword(email, Exceptions.classAndMessage(e));
								} finally {
									monitor.done();
									throw WrappedException.wrap(e);
								}
							}
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