package org.softwareFm.collections.mySoftwareFm;

public interface IForgotPasswordCallback {

	void emailSent(String email, String magicString);

	void failedToSend(String email, String message);

}
