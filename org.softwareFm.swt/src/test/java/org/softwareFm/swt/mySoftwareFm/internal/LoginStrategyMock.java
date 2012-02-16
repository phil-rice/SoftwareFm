/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.mySoftwareFm.internal;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.mySoftwareFm.IChangePasswordCallback;
import org.softwareFm.swt.mySoftwareFm.IForgotPasswordCallback;
import org.softwareFm.swt.mySoftwareFm.ILoginCallback;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.mySoftwareFm.IRequestSaltCallback;
import org.softwareFm.swt.mySoftwareFm.ISignUpCallback;

public class LoginStrategyMock implements ILoginStrategy {

	public final List<String> forgotPasswordEmail = Lists.newList();
	public final List<String> loginEmail = Lists.newList();
	public final List<String> loginPassword = Lists.newList();
	public final AtomicInteger requestSaltCount = new AtomicInteger();
	public final List<String> signupEmail = Lists.newList();
	public final List<String> signupMoniker = Lists.newList();
	public final List<String> signupSalt = Lists.newList();
	public final List<String> signupPassword = Lists.newList();
	public final List<String> signupCrypt = Lists.newList();
	public final List<String> passwordHash = Lists.newList();
	private final List<String> requestEmailSaltEmail = Lists.newList();
	private final List<String> requestEmailSaltSessionSalt = Lists.newList();

	private boolean ok;
	public final String cryptoKey = Crypto.makeKey();
	public final String softwareFmId = "someSoftwareFmId";
	private final String sessionSalt = UUID.randomUUID().toString();
	private final String emailSalt = UUID.randomUUID().toString();
	private boolean emailSaltOk;

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public void setEmailSetOk(boolean ok) {
		this.emailSaltOk = ok;
	}

	@Override
	public void forgotPassword(String email, String sessionSalt, IForgotPasswordCallback callback) {
		forgotPasswordEmail.add(email);
		if (ok)
			callback.emailSent(email);
		else
			callback.failedToSend(email, "someMessage");
	}

	@Override
	public void login(String email, String sessionSalt, String emailSalt, String password, ILoginCallback callback) {
		loginEmail.add(email);
		loginPassword.add(password);
		if (ok)
			callback.loggedIn(new UserData(email, softwareFmId, cryptoKey));
		else
			callback.failedToLogin(email, "someMessage");
	}

	@Override
	public void requestSessionSalt(IRequestSaltCallback callback) {
		requestSaltCount.incrementAndGet();
		if (ok)
			callback.saltReceived(sessionSalt);
		else
			callback.problemGettingSalt("someMessage");
	}

	@Override
	public void signup(String email, String moniker, String sessionSalt, String passwordHash, ISignUpCallback callback) {
		signupEmail.add(email);
		signupSalt.add(sessionSalt);
		signupPassword.add(passwordHash);
		signupMoniker.add(moniker);
		if (ok)
			callback.signedUp(new UserData(email, softwareFmId, cryptoKey));
		else
			callback.failed(email, "someMessage");
	}

	@Override
	public void requestEmailSalt(String email, String sessionSalt, IRequestSaltCallback callback) {
		requestEmailSaltEmail.add(email);
		requestEmailSaltSessionSalt.add(sessionSalt);
		if (emailSaltOk)
			callback.saltReceived(emailSalt);
		else
			callback.problemGettingSalt("someMessage");
	}

	@Override
	public void changePassword(String email, String oldHash, String newHash, IChangePasswordCallback callback) {
		throw new UnsupportedOperationException();
	}

}