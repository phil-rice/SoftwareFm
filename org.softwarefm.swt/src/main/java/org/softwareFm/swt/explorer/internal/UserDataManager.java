package org.softwareFm.swt.explorer.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.softwareFm.swt.explorer.IUserDataListener;
import org.softwareFm.swt.explorer.IUserDataManager;

public class UserDataManager implements IUserDataManager {

	private UserData userData = UserData.blank();
	private final List<IUserDataListener> listeners = new CopyOnWriteArrayList<IUserDataListener>();

	@Override
	public void setUserData(Object  source, UserData userData) {
		this.userData = userData;
		for (IUserDataListener listener : listeners)
			listener.userDataChanged(source, userData);
	}

	@Override
	public UserData getUserData() {
		return userData;
	}

	@Override
	public void addUserDataListener(IUserDataListener userDataListener) {
		listeners.add(userDataListener);
	}

}
