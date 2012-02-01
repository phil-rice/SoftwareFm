package org.softwareFm.collections.mySoftwareFm;

public interface IChangePasswordCallback {

	void changedPassword(String email);

	void failedToChangePassword(String email, String message);

	void cancelChangePassword();

}
