package org.softwareFm.collections.mySoftwareFm;

public interface ILoginDisplayStrategy {

	void showLogin(String sessionSalt);

	void showForgotPassword(String sessionSalt);

	void showSignup(String sessionSalt);

	public static class Utils {
		public static ILoginDisplayStrategy noDisplayStrategy() {
			return new ILoginDisplayStrategy() {

				@Override
				public void showSignup(String sessionSalt) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void showLogin(String sessionSalt) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void showForgotPassword(String sessionSalt) {
					throw new UnsupportedOperationException();
				}
			};
		}

		public static ILoginDisplayStrategy sysoutDisplayStrategy() {
			return new ILoginDisplayStrategy() {

				@Override
				public void showSignup(String sessionSalt) {
					System.out.println("showSignUp: " + sessionSalt);
				}

				@Override
				public void showLogin(String sessionSalt) {
					System.out.println("showLogin: " + sessionSalt);
				}

				@Override
				public void showForgotPassword(String sessionSalt) {
					System.out.println("showForgotPassword: " + sessionSalt);
				}
			};
		}
	}

}
