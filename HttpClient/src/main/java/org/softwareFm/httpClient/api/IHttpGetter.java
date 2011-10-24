package org.softwareFm.httpClient.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.softwareFm.httpClient.api.impl.HttpGetter;
import org.softwareFm.httpClient.response.impl.Response;

public interface IHttpGetter {

	Future<Response> getFromUrl(String type, String url, IHttpGetterCallback callback);

	public static class Utils {
		IHttpGetter makeGetter(ExecutorService service) {
			return new HttpGetter(service);
		}
	}

}
