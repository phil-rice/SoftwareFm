package org.softwareFm.collections.mySoftwareFm;

public interface ILoginStrategy {
	public void login(String email, String password);

	public void cancelLogin();

	public void forgotPassword();

	public void signUp();
}
