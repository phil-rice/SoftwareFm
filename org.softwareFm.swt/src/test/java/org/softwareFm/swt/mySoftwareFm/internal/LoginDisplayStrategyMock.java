package org.softwareFm.swt.mySoftwareFm.internal;

import java.util.List;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.swt.mySoftwareFm.ILoginDisplayStrategy;

public class LoginDisplayStrategyMock implements ILoginDisplayStrategy {

	public final List<String> showLogin = Lists.newList();
	public final List<String> showForgetPassword = Lists.newList();
	public final List<String> showSignup = Lists.newList();
	public final List<String> showChangePassword = Lists.newList();
	public final List<String> initialEmails = Lists.newList();

	@Override
	public void showLogin(String sessionSalt, String initialEmail) {
		showLogin.add(sessionSalt);
		initialEmails.add(initialEmail);
	}

	@Override
	public void showForgotPassword(String sessionSalt, String initialEmail) {
		showForgetPassword.add(sessionSalt);
		initialEmails.add(initialEmail);
	}

	@Override
	public void showSignup(String sessionSalt, String initialEmail) {
		showSignup.add(sessionSalt);
		initialEmails.add(initialEmail);
	}

	@Override
	public void showChangePassword(String email) {
		showChangePassword.add(email);
	}

}
