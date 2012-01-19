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
	private final List<String> requestEmailSaltEmail = Lists.newList();
	private final List<String> requestEmailSaltSessionSalt = Lists.newList();

	private boolean ok;
	public final String cryptoKey = Crypto.makeKey();
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
	public void showLogin(String sessionSalt) {
		showLogin.add(sessionSalt);
	}

	@Override
	public void showForgotPassword(String sessionSalt) {
		showForgetPassword.add(sessionSalt);
	}

	@Override
	public void showSignup(String sessionSalt) {
		showSignup.add(sessionSalt);
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
			callback.loggedIn(email, cryptoKey);
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
	public void signup(String email, String sessionSalt, String cryptoKey, String passwordHash, ISignUpCallback callback) {
		signupEmail.add(email);
		signupSalt.add(sessionSalt);
		signupPassword.add(passwordHash);
		if (ok)
			callback.signedUp(email);
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

}
