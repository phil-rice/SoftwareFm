/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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