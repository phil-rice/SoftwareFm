package org.softwareFm.eclipse.mysoftwareFm;

import java.util.concurrent.Future;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.eclipse.IRequestGroupReportGeneration;

public class RequestGroupReportGeneration implements IRequestGroupReportGeneration {

	private final IHttpClient client;
	private final IResponseCallback callback;

	public RequestGroupReportGeneration(IHttpClient client, IResponseCallback callback) {
		this.client = client;
		this.callback = callback;
	}

	@Override
	public Future<?> request(String groupId, String groupCryptoKey, String month) {
		return client.post(GroupConstants.generateGroupReportPrefix).//
				addParam(GroupConstants.groupIdKey, groupId).//
				addParam(GroupConstants.groupCryptoKey, groupCryptoKey).//
				addParam(GroupConstants.monthKey, month).//
				execute(callback);
	}
}