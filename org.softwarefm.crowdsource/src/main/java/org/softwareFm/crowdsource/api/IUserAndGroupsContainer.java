package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;

public interface IUserAndGroupsContainer extends IContainer {
	<T> T accessGroupReader(IFunction1<IGroupsReader, T> function);

	<T> T accessUserReader(IFunction1<IUserReader, T> function);

	<T> T accessUserMembershipReader(IFunction2<IGroupsReader, IUserMembershipReader, T> function);

	void modifyUser(ICallback<IUser> callback);

	void modifyGroups(ICallback<IGroups> callback);

	void modifyUserMembership(ICallback2<IGroups, IUserMembership> callback);
}
