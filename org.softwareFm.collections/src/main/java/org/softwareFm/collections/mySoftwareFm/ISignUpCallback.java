package org.softwareFm.collections.mySoftwareFm;

public interface ISignUpCallback {

	void signedUp(String email, String crypto, String softwareFmId);

	void failed(String email, String message);

}
