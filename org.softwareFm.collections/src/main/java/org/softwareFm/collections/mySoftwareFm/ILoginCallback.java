package org.softwareFm.collections.mySoftwareFm;

public interface ILoginCallback {

	void loggedIn(String email, String cryptoKey);

	void failedToLogin(String email, String message);

}
