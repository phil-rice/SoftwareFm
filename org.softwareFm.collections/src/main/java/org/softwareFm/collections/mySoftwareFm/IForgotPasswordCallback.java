package org.softwareFm.collections.mySoftwareFm;

public interface IForgotPasswordCallback {

	void emailSent(String email);

	void failedToSend(String email, String message);

}
