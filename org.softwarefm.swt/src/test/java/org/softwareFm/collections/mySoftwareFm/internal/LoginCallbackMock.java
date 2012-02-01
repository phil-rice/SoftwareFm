package org.softwareFm.collections.mySoftwareFm.internal;

import java.util.List;

import org.softwareFm.collections.explorer.internal.UserData;
import org.softwareFm.collections.mySoftwareFm.ILoginCallback;
import org.softwareFm.common.collections.Lists;

public class LoginCallbackMock implements ILoginCallback {

	public final List<UserData> loggedInUserData = Lists.newList();
	public final List<String> failedEmail = Lists.newList();
	public final List<String> failedMessage = Lists.newList();

	@Override
	public void loggedIn(UserData userData) {
		loggedInUserData.add(userData);
	}

	@Override
	public void failedToLogin(String email, String message) {
		failedEmail.add(email);
		failedMessage.add(message);
	}

}
