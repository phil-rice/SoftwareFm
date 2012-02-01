package org.softwareFm.swt.mySoftwareFm;

import org.softwareFm.swt.explorer.internal.UserData;

public interface ISignUpCallback {

	void signedUp(UserData userData);

	void failed(String email, String message);

}
