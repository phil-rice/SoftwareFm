package org.softwareFm.swt.explorer.internal;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwareFm.swt.explorer.IUserDataListener;
import org.softwareFm.swt.explorer.IUserDataManager;

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
		manager = new UserDataManager();
	}
}
