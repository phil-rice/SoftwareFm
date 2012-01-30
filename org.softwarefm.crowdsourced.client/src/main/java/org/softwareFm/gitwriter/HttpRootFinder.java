package org.softwareFm.gitwriter;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.requests.MemoryResponseCallback;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.url.Urls;

public class HttpRootFinder implements IFunction1<String, String> {

	private final IHttpClient client;
	
	public HttpRootFinder(IHttpClient client) {
		this.client = client;
	}

	@Override
	public String apply(String from) throws Exception {
		MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
		client.post(Urls.compose(CommonConstants.findRepositoryBasePrefix, from)).execute(memoryCallback);
		return memoryCallback.response.asString();
	}

}
