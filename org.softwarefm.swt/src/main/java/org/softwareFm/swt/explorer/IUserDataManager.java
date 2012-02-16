package org.softwareFm.swt.explorer;

import org.softwareFm.swt.explorer.internal.UserData;
import org.softwareFm.swt.explorer.internal.UserDataManager;

public interface IUserDataManager {

	void setUserData(Object source, UserData userData);

	UserData getUserData();

	void addUserDataListener(IUserDataListener userDataListener);

	public static class Utils {
		public static IUserDataManager userDataManager() {
			return new UserDataManager();
		}
	}
}