package org.softwareFm.crowdsource.user.internal;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.user.GroupOperationResult;
import org.softwareFm.crowdsource.api.user.IGroupOperations;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;

public class ClientGroupOperations implements IGroupOperations {

	private final IContainer readWriteApi;

	public ClientGroupOperations(IContainer readWriteApi) {
		this.readWriteApi = readWriteApi;
	}

	@Override
	public void createGroup(final String softwareFmId, final String userCryptoKey, final String groupName, final String fromEmail, final String takeOnEmailList, final String takeOnSubjectPattern, final String takeOnEmailPattern, final ICallback<GroupOperationResult> callback) {
		readWriteApi.access(IHttpClient.class, new ICallback<IHttpClient>() {
			@Override
			public void process(IHttpClient client) throws Exception {
				client.post(GroupConstants.takeOnCommandPrefix).//
						addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
						addParam(GroupConstants.groupNameKey, groupName).//
						addParam(GroupConstants.takeOnEmailPattern, takeOnEmailPattern).//
						addParam(GroupConstants.takeOnEmailListKey, takeOnEmailList).//
						addParam(GroupConstants.takeOnSubjectKey, takeOnSubjectPattern).//
						addParam(GroupConstants.takeOnFromKey, fromEmail).//
						execute(callCallback(callback));
			}
		});

	}

	@Override
	public void inviteToGroup(final String softwareFmId, final String userCryptoKey, final String groupId, final String fromEmail, final String takeOnEmailList, final String takeOnSubjectPattern, final String takeOnEmailPattern, final ICallback<GroupOperationResult> callback) {
		readWriteApi.access(IHttpClient.class, new ICallback<IHttpClient>() {
			@Override
			public void process(IHttpClient client) throws Exception {
				client.post(GroupConstants.inviteCommandPrefix).//
						addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
						addParam(GroupConstants.groupIdKey, groupId).//
						addParam(GroupConstants.takeOnEmailPattern, takeOnEmailPattern).//
						addParam(GroupConstants.takeOnEmailListKey, takeOnEmailList).//
						addParam(GroupConstants.takeOnSubjectKey, takeOnSubjectPattern).//
						addParam(GroupConstants.takeOnFromKey, fromEmail).//
						execute(callCallback(callback, groupId));
			}
		});

	}

	@Override
	public void acceptInvite(final String softwareFmId, String userCryptoKey, final String groupId, final ICallback<GroupOperationResult> callback) {
		readWriteApi.access(IHttpClient.class, new ICallback<IHttpClient>() {
			@Override
			public void process(IHttpClient client) throws Exception {
				client.post(GroupConstants.acceptInvitePrefix).//
						addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
						addParam(GroupConstants.groupIdKey, groupId).//
						addParam(GroupConstants.membershipStatusKey, GroupConstants.memberStatus).//
						execute(callCallback(callback, groupId));
			}
		});
	}

	@Override
	public void leaveGroup(final String softwareFmId, String userCryptoKey, final String groupId, final ICallback<GroupOperationResult> callback) {
		readWriteApi.access(IHttpClient.class, new ICallback<IHttpClient>() {
			@Override
			public void process(IHttpClient client) throws Exception {
				client.post(GroupConstants.leaveGroupPrefix).//
						addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
						addParam(GroupConstants.groupIdKey, groupId).//
						execute(callCallback(callback, groupId));
			}
		});
	}

	@Override
	public void kickFromGroup(final String softwareFmId, String userCryptoKey, final String groupId, final String otherIds, final ICallback<GroupOperationResult> callback) {
		readWriteApi.access(IHttpClient.class, new ICallback<IHttpClient>() {
			@Override
			public void process(IHttpClient client) throws Exception {
				client.post(GroupConstants.kickFromGroupPrefix).//
						addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
						addParam(GroupConstants.objectSoftwareFmId, otherIds).//
						addParam(GroupConstants.groupIdKey, groupId).//
						execute(callCallback(callback, groupId));
			}
		});
	}

	private IResponseCallback callCallback(final ICallback<GroupOperationResult> callback, final String groupId) {
		return new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				if (CommonConstants.okStatusCodes.contains(response.statusCode()))
					ICallback.Utils.call(callback, GroupOperationResult.groupId(groupId));
				else
					ICallback.Utils.call(callback, GroupOperationResult.error(response.asString()));
			}
		};
	}

	private IResponseCallback callCallback(final ICallback<GroupOperationResult> callback) {
		return new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				if (CommonConstants.okStatusCodes.contains(response.statusCode()))
					ICallback.Utils.call(callback, GroupOperationResult.groupId(response.asString()));
				else
					ICallback.Utils.call(callback, GroupOperationResult.error(response.asString()));
			}
		};
	}

}
