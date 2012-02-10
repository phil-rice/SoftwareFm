package org.softwareFm.swt.explorer;

import org.softwareFm.swt.explorer.internal.UserData;

public interface IShowMyGroups {

	void show(UserData data);

	public static class Utils {

		public static IShowMyGroups sysoutShowMyGroups() {
			return new IShowMyGroups() {
				@Override
				public void show(UserData data) {
					System.out.println("My groups: " + data);
				}
			};
		}

		public static IShowMyGroups exceptionShowMyGroups() {
			return new IShowMyGroups() {
				@Override
				public void show(UserData data) {
					throw new UnsupportedOperationException();
				}
			};
		}

	}
}
