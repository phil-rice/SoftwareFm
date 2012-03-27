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
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

public interface IUserAndGroupsContainer extends IContainer {
	<T> ITransaction<T> accessGroupReader(IFunction1<IGroupsReader, T> function, ICallback<T> resultCallback);

	<T> ITransaction<T> accessUserReader(IFunction1<IUserReader, T> function, ICallback<T> resultCallback);

	<T> ITransaction<T> accessUserMembershipReader(IFunction2<IGroupsReader, IUserMembershipReader, T> function, ICallback<T> resultCallback);

	ITransaction<Void> accessUser(ICallback<IUser> callback);

	ITransaction<Void> accessGroups(ICallback<IGroups> callback);

	ITransaction<Void> accessUserMembership(ICallback2<IGroups, IUserMembership> callback);
}
