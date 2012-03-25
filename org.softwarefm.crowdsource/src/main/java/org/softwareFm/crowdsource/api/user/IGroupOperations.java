package org.softwareFm.crowdsource.api.user;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.user.internal.ClientGroupOperations;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;

public interface IGroupOperations {
	void createGroup(String softwareFmId, String userCryptoKey, String groupName, String fromEmail, String takeOnEmailList, String takeOnSubjectPattern, String takeOnEmailPattern, ICallback<GroupOperationResult> callback);

	void inviteToGroup(String softwareFmId, String userCryptoKey, String groupId, String fromEmail, String takeOnEmailList, String takeOnSubjectPattern, String takeOnEmailPattern, ICallback<GroupOperationResult> callback);

	void acceptInvite(String softwareFmId, String userCryptoKey, String groupId, ICallback<GroupOperationResult> callback);

	void leaveGroup(String softwareFmId, String userCryptoKey, String groupId, ICallback<GroupOperationResult> callback);

	void kickFromGroup(String softwareFmId, String userCryptoKey, String groupId, String memberId, ICallback<GroupOperationResult> callback);

	public static class Utils{
		public static IGroupOperations clientGroupOperations(IContainer readWriteApi, long timeOutMs){
			return new ClientGroupOperations(readWriteApi);
		}
	}
	
}
