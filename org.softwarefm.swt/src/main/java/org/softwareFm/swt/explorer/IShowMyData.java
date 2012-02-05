package org.softwareFm.swt.explorer;

import org.softwareFm.swt.explorer.internal.UserData;

public interface IShowMyData {

	void show(UserData userData);

	public static class Utils {
		public static IShowMyData exceptionShowMyData() {
			return new IShowMyData() {
				@Override
				public void show(UserData userData) {
					throw new RuntimeException();
				}
			};
		}

		public static IShowMyData sysout() {
			return new IShowMyData() {
				@Override
				public void show(UserData userData) {
					System.out.println("Show my data");
				}
			};
		}
	}
}
