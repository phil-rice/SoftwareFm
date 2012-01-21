package org.softwareFm.collections.mySoftwareFm;

public interface ILoginDisplayStrategy {

	void showLogin(String sessionSalt, String initialEmail);

	void showForgotPassword(String sessionSalt, String initialEmail);

	void showSignup(String sessionSalt, String initialEmail);

	public static class Utils {
		public static ILoginDisplayStrategy noDisplayStrategy() {
			return new ILoginDisplayStrategy() {

				@Override
				public void showSignup(String sessionSalt, String initialEmail) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void showLogin(String sessionSalt, String initialEmail) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void showForgotPassword(String sessionSalt, String initialEmail) {
					throw new UnsupportedOperationException();
				}
			};
		}

		public static ILoginDisplayStrategy sysoutDisplayStrategy() {
			return new ILoginDisplayStrategy() {

				@Override
				public void showSignup(String sessionSalt, String initialEmail) {
					System.out.println("showSignUp: " + sessionSalt+", " + initialEmail);
				}

				@Override
				public void showLogin(String sessionSalt, String initialEmail) {
					System.out.println("showLogin: " + sessionSalt+", " + initialEmail);
				}

				@Override
				public void showForgotPassword(String sessionSalt, String initialEmail) {
					System.out.println("showForgotPassword: " + sessionSalt+", " + initialEmail);
				}
			};
		}
	}

}
