/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.internal;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.jarAndClassPath.api.IUserDataListener;
import org.softwareFm.jarAndClassPath.api.IUserDataManager;

public class UserDataManagerTest extends TestCase {
	private IUserDataManager manager;
	private final UserData one = new UserData("a", "b", "c");
	private final UserData two = new UserData("d", "e", "f");

	public void testStartsWithBlank() {
		assertEquals(UserData.blank(), manager.getUserData());
	}

	public void testSetGet() {
		manager.setUserData(this, one);
		assertEquals(one, manager.getUserData());
		assertEquals(one, manager.getUserData());

		manager.setUserData(this, two);
		assertEquals(two, manager.getUserData());
		assertEquals(two, manager.getUserData());
	}

	public void testNotifiesListenersWhenSet() {
		IUserDataListener mock1 = makeMock();
		IUserDataListener mock2 = makeMock();
		manager.addUserDataListener(mock1);
		manager.addUserDataListener(mock2);

		manager.setUserData(this, one);
		manager.setUserData(this, two);
		EasyMock.verify(mock1, mock2);
	}

	protected IUserDataListener makeMock() {
		IUserDataListener mock = EasyMock.createMock(IUserDataListener.class);
		mock.userDataChanged(this, one);
		mock.userDataChanged(this, two);
		EasyMock.replay(mock);
		return mock;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = IUserDataManager.Utils.userDataManager();
	}
}