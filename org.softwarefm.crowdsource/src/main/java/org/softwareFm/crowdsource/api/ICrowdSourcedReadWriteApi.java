package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;

public interface ICrowdSourcedReadWriteApi extends ICrowdSourcedReaderApi {

	


	void modifyUser(ICallback<IUser> callback);

	void modifyGroups(ICallback<IGroups> callback);

	void modifyUserMembership(ICallback2<IGroups, IUserMembership> callback);

	void modifyComments(ICallback<IComments> callback);

	<API> void modify(Class<API> clazz, ICallback<API> callback);

	<A1, A2> void modify(Class<A1> clazz1, Class<A2> clazz2, ICallback2<A1, A2> callback);

	<A1, A2, A3> void modify(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, ICallback3<A1, A2, A3> callback);
}
