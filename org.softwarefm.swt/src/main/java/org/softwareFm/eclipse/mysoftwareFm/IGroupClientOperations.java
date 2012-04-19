/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.mysoftwareFm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.runnable.Runnables;
import org.softwareFm.eclipse.mysoftwareFm.internal.GroupClientOperations;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.IMasterDetailSocial;

public interface IGroupClientOperations {
	Runnable createGroup(UserData userData, ICallback<String> added);

	Runnable inviteToGroup(UserData userData, Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> showMyGroups);

	Runnable acceptInvitation(UserData userData, Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> showMyGroups);

	Runnable leaveGroup(UserData userData, ICallback<String> showMyGroups, Callable<IdNameAndStatus> idNameStatusGetter);

	Runnable kickMember(UserData userData, Callable<IdNameAndStatus> idNameStatusGetter, Callable<List<Map<String, Object>>> objectMapGetter, ICallback<String> showMyGroups);

	public static class Utils {
		public static IGroupClientOperations groupOperations(IMasterDetailSocial masterDetailSocial, CardConfig cardConfig, IContainer readWriteApi) {
			return new GroupClientOperations(masterDetailSocial, cardConfig, readWriteApi);
		}

		public static IGroupClientOperations exception() {
			return new IGroupClientOperations() {

				@Override
				public Runnable createGroup(UserData userData, ICallback<String> added) {
					return Runnables.exception();
				}

				@Override
				public Runnable inviteToGroup(UserData userData, final Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> invited) {
					return Runnables.exception();
				}

				@Override
				public Runnable acceptInvitation(UserData userData, Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> showMyGroups) {
					return Runnables.exception();
				}

				@Override
				public Runnable leaveGroup(UserData userData, ICallback<String> showMyGroups, final Callable<IdNameAndStatus> idNameStatusGetter) {
					return Runnables.exception();
				}

				@Override
				public Runnable kickMember(UserData userData, Callable<IdNameAndStatus> idNameStatusGetter, Callable<List<Map<String, Object>>> objectMembershipGetter, ICallback<String> showMyGroups) {
					return Runnables.exception();
				}
			};
		}
	}

}