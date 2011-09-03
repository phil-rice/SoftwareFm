package org.softwareFm.arc4eclipseRepository.api;

import java.util.Map;

import org.softwareFm.httpClient.response.IResponse;

public interface IArc4EclipseLogger {

	void sendingRequest(String method, String url, Map<String, Object> parameters, Map<String, Object> context);

	void receivedReply(IResponse response, Object data, Map<String, Object> context);

}
