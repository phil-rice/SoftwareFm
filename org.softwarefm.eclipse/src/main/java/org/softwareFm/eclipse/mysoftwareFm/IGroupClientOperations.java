package org.softwareFm.eclipse.mysoftwareFm;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.eclipse.mysoftwareFm.internal.GroupClientOperations;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.internal.UserData;

public interface IGroupClientOperations {
	Runnable createGroup(UserData userData, ICallback<String> added);

	Runnable inviteToGroup(UserData userData, final Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> showMyGroups);

	Runnable acceptInvitation(UserData userData, Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> showMyGroups);

	Runnable deleteGroup(UserData userData);

	Runnable kickMember(UserData userData, final Callable<IdNameAndStatus> idNameStatusGetter, Callable<Map<String, Object>> objectMembershipGetter, ICallback<String> showMyGroups);

	public static class Utils {
		public static IGroupClientOperations groupOperations(IMasterDetailSocial masterDetailSocial, CardConfig cardConfig, IHttpClient client) {
			return new GroupClientOperations(masterDetailSocial, cardConfig, client);
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
				public Runnable deleteGroup(UserData userData) {
					return Runnables.exception();
				}

				@Override
				public Runnable kickMember(UserData userData, Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> showMyGroups) {
					return Runnables.exception();
				}
			};
		}
	}

}
