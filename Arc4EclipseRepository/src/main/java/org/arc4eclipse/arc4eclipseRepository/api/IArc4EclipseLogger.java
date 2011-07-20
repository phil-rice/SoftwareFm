package org.arc4eclipse.arc4eclipseRepository.api;

import java.util.Map;

import org.arc4eclipse.httpClient.response.IResponse;

public interface IArc4EclipseLogger {

	void sendingRequest(String method, String url, Map<String, Object> parameters);

	void receivedReply(IResponse response, Object data);

}
