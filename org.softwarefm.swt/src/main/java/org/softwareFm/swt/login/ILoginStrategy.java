/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.login;

import java.util.UUID;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Display;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.login.internal.LoginStrategy;

public interface ILoginStrategy {

	ITransaction<Void> requestSessionSalt(IRequestSaltCallback callback);

	ITransaction<Void> signup(String email, String moniker, String sessionSalt, String passwordHash, ISignUpCallback callback);

	ITransaction<Void> login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback);

	ITransaction<Void> forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback);

	ITransaction<Void> requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback);

	ITransaction<Void> changePassword(String email, String oldHash, String newHash, IChangePasswordCallback callback);

	public static class Utils {

		public static ILoginStrategy softwareFmLoginStrategy(final Display display, final IContainer container, final long timeOutMs) {
			return new LoginStrategy(container, timeOutMs);
		}

		public static ILoginStrategy sysoutLoginStrategy() {
			return new ILoginStrategy() {
				Callable<String> softwareFmIdGenerator = Callables.patternWithCount("softwareFmid{0}");

				@Override
				public ITransaction<Void> forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback) {
					System.out.println("Sending 'forgot password' to server");
					callback.emailSent(email);
					return ITransaction.Utils.doneTransaction(null);
				}

				@Override
				public ITransaction<Void> login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback) {
					String key = Crypto.makeKey();
					System.out.println("Sending 'login' to server, would have received key: " + key);
					callback.loggedIn(new UserData(email, Callables.call(softwareFmIdGenerator), key));
					return ITransaction.Utils.doneTransaction(null);
				}

				@Override
				public ITransaction<Void> requestSessionSalt(IRequestSaltCallback callback) {
					System.out.println("Requesting salt");
					callback.saltReceived(UUID.randomUUID().toString());
					return ITransaction.Utils.doneTransaction(null);
				}

				@Override
				public ITransaction<Void> signup(String email, String moniker, String sessionSalt, String passwordHash, ISignUpCallback callback) {
					System.out.println("Signing up: " + email + ", " + moniker + ", " + sessionSalt + ", " + passwordHash);
					callback.signedUp(new UserData(email, Callables.call(softwareFmIdGenerator), Crypto.makeKey()));
					return ITransaction.Utils.doneTransaction(null);
				}

				@Override
				public ITransaction<Void> requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback) {
					System.out.println("requesting email salt: " + email + ", " + sessionSalt);
					callback.saltReceived(UUID.randomUUID().toString());
					return ITransaction.Utils.doneTransaction(null);
				}

				@Override
				public ITransaction<Void> changePassword(String email, String oldHash, String newHash, IChangePasswordCallback callback) {
					System.out.println("changePassword: " + email + ", " + oldHash + ", " + newHash);
					callback.changedPassword(email);
					return ITransaction.Utils.doneTransaction(null);
				}

			};

		}

		public static ILoginStrategy noLoginStrategy() {
			return new ILoginStrategy() {

				@Override
				public ITransaction<Void> signup(String email, String moniker, String sessionSalt, String passwordHash, ISignUpCallback callback) {
					throw new UnsupportedOperationException();
				}

				@Override
				public ITransaction<Void> requestSessionSalt(IRequestSaltCallback callback) {
					callback.problemGettingSalt("Unsupported");
					return ITransaction.Utils.doneTransaction(null);
				}

				@Override
				public ITransaction<Void> requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback) {
					throw new UnsupportedOperationException();
				}

				@Override
				public ITransaction<Void> login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback) {
					throw new UnsupportedOperationException();
				}

				@Override
				public ITransaction<Void> forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback) {
					throw new UnsupportedOperationException();
				}

				@Override
				public ITransaction<Void> changePassword(String email, String oldHash, String newHash, IChangePasswordCallback callback) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

}