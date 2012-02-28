package org.softwareFm.eclipse.mysoftwareFm;

import junit.framework.Assert;

import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.common.runnable.Runnables.CountRunnable;
import org.softwareFm.swt.explorer.internal.UserData;

public class GroupClientOperationsMock implements IGroupClientOperations {

	private UserData userData;
	public CountRunnable create = Runnables.count();
	public CountRunnable invite= Runnables.count();
	public CountRunnable accept= Runnables.count();
	public CountRunnable delete= Runnables.count();

	@Override
	public Runnable createGroup(UserData userData, Runnable added) {
		checkUserData(userData);
		added.run();
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
		return invite;
	}

	@Override
	public Runnable acceptInvitation(UserData userData) {
		checkUserData(userData);
		return accept;
	}

	@Override
	public Runnable deleteGroup(UserData userData) {
		checkUserData(userData);
		return delete;
	}

}
