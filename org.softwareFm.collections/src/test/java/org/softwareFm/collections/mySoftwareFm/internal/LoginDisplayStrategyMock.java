package org.softwareFm.collections.mySoftwareFm.internal;

import java.util.List;

import org.softwareFm.collections.mySoftwareFm.ILoginDisplayStrategy;
import org.softwareFm.utilities.collections.Lists;

public class LoginDisplayStrategyMock implements ILoginDisplayStrategy {

	public final List<String> showLogin = Lists.newList();
	public final List<String> showForgetPassword = Lists.newList();
	public final List<String> showSignup = Lists.newList();

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

}
