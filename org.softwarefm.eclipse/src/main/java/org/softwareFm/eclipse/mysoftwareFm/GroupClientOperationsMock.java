package org.softwareFm.eclipse.mysoftwareFm;

import junit.framework.Assert;

import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.common.runnable.Runnables.CountRunnable;
import org.softwareFm.swt.explorer.internal.UserData;

public class GroupClientOperationsMock implements IGroupClientOperations {

	private UserData userData;
	public CountRunnable create;
	public CountRunnable invite;
	public CountRunnable accept;

	@Override
	public Runnable createGroup(UserData userData) {
		checkUserData(userData);
		create = Runnables.count();
		return create;
	}

	protected void checkUserData(UserData userData) {
		if (this.userData == null)
			this.userData = userData;
		Assert.assertEquals(this.userData, userData);
	}

	@Override
	public Runnable inviteToGroup(UserData userData) {
		checkUserData(userData);
		invite = Runnables.count();
		return invite;
	}

	@Override
	public Runnable acceptInvitation(UserData userData) {
		checkUserData(userData);
		accept = Runnables.count();
		return accept;
	}

}
