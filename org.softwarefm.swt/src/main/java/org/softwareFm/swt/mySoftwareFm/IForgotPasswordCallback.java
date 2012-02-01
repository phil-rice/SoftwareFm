package org.softwareFm.swt.mySoftwareFm;

public interface IForgotPasswordCallback {

	void emailSent(String email);

	void failedToSend(String email, String message);

}
