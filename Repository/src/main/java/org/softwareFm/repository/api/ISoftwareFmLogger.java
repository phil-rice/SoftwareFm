package org.softwareFm.repository.api;

import java.util.Map;

import org.softwareFm.httpClient.response.IResponse;

public interface ISoftwareFmLogger {

	void sendingRequest(String method, String url, Map<String, Object> parameters, Map<String, Object> context);

	void receivedReply(IResponse response, Object data, Map<String, Object> context);

}
