package org.softwareFm.collections.mySoftwareFm;

import org.softwareFm.collections.explorer.internal.UserData;

public interface ILoginCallback {

	void loggedIn(UserData userData);

	void failedToLogin(String email, String message);

}
