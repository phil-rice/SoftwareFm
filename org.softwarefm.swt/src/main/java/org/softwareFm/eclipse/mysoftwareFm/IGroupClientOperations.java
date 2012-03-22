package org.softwareFm.eclipse.mysoftwareFm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ICrowdSourcedReadWriteApi;
import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.runnable.Runnables;
import org.softwareFm.eclipse.mysoftwareFm.internal.GroupClientOperations;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.explorer.IMasterDetailSocial;

public interface IGroupClientOperations {
	Runnable createGroup(UserData userData, Callable<CardConfig> cardConfigGetter, ICallback<String> added);

	Runnable inviteToGroup(UserData userData,   Callable<CardConfig> cardConfigGetter, Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> showMyGroups);

	Runnable acceptInvitation(UserData userData,  Callable<CardConfig> cardConfigGetter, Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> showMyGroups);

	Runnable leaveGroup(UserData userData,  Callable<CardConfig> cardConfigGetter, ICallback<String> showMyGroups,  Callable<IdNameAndStatus> idNameStatusGetter);

	Runnable kickMember(UserData userData, Callable<CardConfig> cardConfigGetter,   Callable<IdNameAndStatus> idNameStatusGetter, Callable<List<Map<String, Object>>> objectMapGetter, ICallback<String> showMyGroups);

	public static class Utils {
		public static IGroupClientOperations groupOperations(IMasterDetailSocial masterDetailSocial, ICrowdSourcedReadWriteApi readWriteApi) {
			return new GroupClientOperations(masterDetailSocial, readWriteApi);
		}

		public static IGroupClientOperations exception() {
			return new IGroupClientOperations() {

				@Override
				public Runnable createGroup(UserData userData, Callable<CardConfig> cardConfigGetter,  ICallback<String> added) {
					return Runnables.exception();
				}

				@Override
				public Runnable inviteToGroup(UserData userData, Callable<CardConfig> cardConfigGetter,  final Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> invited) {
					return Runnables.exception();
				}

				@Override
				public Runnable acceptInvitation(UserData userData, Callable<CardConfig> cardConfigGetter,  Callable<IdNameAndStatus> idNameStatusGetter, ICallback<String> showMyGroups) {
					return Runnables.exception();
				}

				@Override
				public Runnable leaveGroup(UserData userData,  Callable<CardConfig> cardConfigGetter, ICallback<String> showMyGroups, final Callable<IdNameAndStatus> idNameStatusGetter) {
					return Runnables.exception();
				}

				@Override
				public Runnable kickMember(UserData userData, Callable<CardConfig> cardConfigGetter,  Callable<IdNameAndStatus> idNameStatusGetter, Callable<List<Map<String, Object>>> objectMembershipGetter, ICallback<String> showMyGroups) {
					return Runnables.exception();
				}
			};
		}
	}

}
