package org.softwareFm.swt.explorer;

import org.softwareFm.swt.explorer.internal.UserData;

public interface IShowMyPeople {
	void showMyPeople(UserData userData, String groupId, String artifactId);

	public static class Utils {
		public static IShowMyPeople sysoutShowMyPeople() {
			return new IShowMyPeople() {
				@Override
				public void showMyPeople(UserData userData, String groupId, String artifactId) {
					System.out.println("ShowMyPeople: " + userData + ", " + groupId + ", " + artifactId);
				}
			};
		}

		public static IShowMyPeople exceptionShowMyPeople() {
			return new IShowMyPeople() {
				@Override
				public void showMyPeople(UserData userData, String groupId, String artifactId) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

}
