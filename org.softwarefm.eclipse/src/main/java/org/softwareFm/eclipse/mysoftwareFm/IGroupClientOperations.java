package org.softwareFm.eclipse.mysoftwareFm;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.eclipse.mysoftwareFm.internal.GroupClientOperations;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.internal.UserData;

public interface IGroupClientOperations {
	Runnable createGroup(UserData userData);

	Runnable inviteToGroup(UserData userData);

	Runnable acceptInvitation(UserData userData);

	public static class Utils {
		public static IGroupClientOperations groupOperations(IMasterDetailSocial masterDetailSocial, CardConfig cardConfig, IHttpClient client) {
			return new GroupClientOperations(masterDetailSocial, cardConfig, client);
		}

		public static IGroupClientOperations exception() {
			return new IGroupClientOperations() {

				@Override
				public Runnable createGroup(UserData userData) {
					return Runnables.exception();
				}

				@Override
				public Runnable inviteToGroup(UserData userData) {
					return Runnables.exception();
				}

				@Override
				public Runnable acceptInvitation(UserData userData) {
					return Runnables.exception();
				}
			};
		}

		public static GroupClientOperationsMock mock() {
			return new GroupClientOperationsMock();
		}
	}

}
