package org.softwareFm.collections.mySoftwareFm.internal;

import java.util.List;

import org.softwareFm.collections.mySoftwareFm.ILoginCallback;
import org.softwareFm.utilities.collections.Lists;

public class LoginCallbackMock implements ILoginCallback {

	public final List<String> loggedInEmail = Lists.newList();
	public final List<String> loggedInCrypto = Lists.newList();
	public final List<String> failedEmail = Lists.newList();
	public final List<String> failedMessage = Lists.newList();

	@Override
	public void loggedIn(String email, String cryptoKey) {
		loggedInEmail.add(email);
		loggedInCrypto.add(cryptoKey);

	}

	@Override
	public void failedToLogin(String email, String message) {
		failedEmail.add(email);
		failedMessage.add(message);
	}

}
