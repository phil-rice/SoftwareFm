package org.softwareFm.swt.mySoftwareFm;


public interface IShowMessage {

	void showMessage(String cardType, String title, String message);

	public static class Utils {

		public static IShowMessage sysout() {
			return new IShowMessage() {
				@Override
				public void showMessage(String cardType, String title, String message) {
					System.out.println(title + " " + message);
				}
			};
		}

	}

}
