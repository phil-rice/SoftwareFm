package org.softwareFm.collections.mySoftwareFm;

import org.softwareFm.collections.explorer.internal.UserData;

public interface ISignUpCallback {

	void signedUp(UserData userData);

	void failed(String email, String message);

}
