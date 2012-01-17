package org.softwareFm.collections.mySoftwareFm;

public interface ISignUpCallback {

	void signedUp(String email);

	void failed(String email, String message);

}
