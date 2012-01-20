package org.softwareFm.collections.mySoftwareFm;

public interface ISignUpCallback {

	void signedUp(String email, String crypto);

	void failed(String email, String message);

}
