package org.softwareFm.httpClient.api;

import org.softwareFm.httpClient.response.IResponse;

public interface IHttpGetterCallback {

	void processGet(String url, IResponse response);
}
