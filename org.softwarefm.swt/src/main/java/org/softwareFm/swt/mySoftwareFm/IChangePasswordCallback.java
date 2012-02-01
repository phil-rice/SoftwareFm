package org.softwareFm.swt.mySoftwareFm;

public interface IChangePasswordCallback {

	void changedPassword(String email);

	void failedToChangePassword(String email, String message);

	void cancelChangePassword();

}
