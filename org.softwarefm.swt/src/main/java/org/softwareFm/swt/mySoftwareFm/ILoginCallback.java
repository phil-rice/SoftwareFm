package org.softwareFm.swt.mySoftwareFm;

import org.softwareFm.swt.explorer.internal.UserData;

public interface ILoginCallback {

	void loggedIn(UserData userData);

	void failedToLogin(String email, String message);

}
