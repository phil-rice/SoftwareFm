package org.softwareFm.collections.mySoftwareFm.internal;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.collections.mySoftwareFm.IForgotPasswordCallback;
import org.softwareFm.collections.mySoftwareFm.ILoginCallback;
import org.softwareFm.collections.mySoftwareFm.ILoginStrategy;
import org.softwareFm.collections.mySoftwareFm.IRequestSaltCallback;
import org.softwareFm.collections.mySoftwareFm.ISignUpCallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.crypto.Crypto;

public class LoginStrategyMock implements ILoginStrategy {

	public final List<String> showLogin = Lists.newList();
	public final List<String> showForgetPassword = Lists.newList();
	public final List<String> showSignup = Lists.newList();
	public final List<String> forgotPasswordEmail = Lists.newList();
	public final List<String> loginEmail = Lists.newList();
	public final List<String> loginPassword = Lists.newList();
	public final AtomicInteger requestSaltCount = new AtomicInteger();
	public final List<String> signupEmail = Lists.newList();
	public final List<String> signupSalt = Lists.newList();
	public final List<String> signupPassword = Lists.newList();
	public final List<String> signupCrypt = Lists.newList();
	public final List<String> passwordHash = Lists.newList();
	private boolean ok;
	public final String cryptoKey = Crypto.makeKey();
	private final String salt = UUID.randomUUID().toString();

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	@Override
	public void showLogin(String salt) {
		showLogin.add(salt);
	}

	@Override
	public void showForgotPassword(String salt) {
		showForgetPassword.add(salt);
	}

	@Override
	public void showSignup(String salt) {
		showSignup.add(salt);
	}

	@Override
	public void forgotPassword(String email, IForgotPasswordCallback callback) {
		forgotPasswordEmail.add(email);
		if (ok)
			callback.emailSent(email);
		else
			callback.failedToSend(email, "someMessage");
	}

	@Override
	public void login(String email, String password, ILoginCallback callback) {
		loginEmail.add(email);
		loginPassword.add(password);
		if (ok)
			callback.loggedIn(email, cryptoKey);
		else
			callback.failedToLogin(email, "someMessage");
	}

	@Override
	public void requestSalt(IRequestSaltCallback callback) {
		requestSaltCount.incrementAndGet();
		if (ok)
			callback.saltReceived(salt);
		else
			callback.problemGettingSalt("someMessage");
	}

	@Override
	public void signup(String email, String salt, String cryptoKey, String passwordHash, ISignUpCallback callback) {
		signupEmail.add(email);
		signupSalt.add(salt);
		signupPassword.add(passwordHash);
	}

}
