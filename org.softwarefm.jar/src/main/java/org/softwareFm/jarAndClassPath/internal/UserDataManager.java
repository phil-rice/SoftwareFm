/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.jarAndClassPath.api.IUserDataListener;
import org.softwareFm.jarAndClassPath.api.IUserDataManager;

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