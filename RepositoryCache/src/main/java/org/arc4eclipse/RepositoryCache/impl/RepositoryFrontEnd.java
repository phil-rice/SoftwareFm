package org.arc4eclipse.RepositoryCache.impl;

import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.arc4eclipse.RepositoryCache.IAspectToParameters;
import org.arc4eclipse.RepositoryCache.IRepositoryFrontEnd;
import org.arc4eclipse.RepositoryConstants.RepositoryCacheConstants;
import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.json.Json;

public class RepositoryFrontEnd implements IRepositoryFrontEnd {

	private final IHttpClient client;
	private final IAspectToParameters parameterMaker;

	public RepositoryFrontEnd(IHttpClient client) {
		this(client, new AspectToParameters());
	}

	public RepositoryFrontEnd(IHttpClient client, IAspectToParameters parameters) {
		this.client = client;
		this.parameterMaker = parameters;
	}

	@Override
	public void get(String url, final ICallback<Map<String, Object>> callback) {
		client.get(url + ".json").execute(new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				try {
					String string = response.asString();
					Map<String, Object> result = parameterMaker.makeFrom(string);
					callback.process(result);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
	}

	@Override
	public void post(String url, Map<String, Object> map, IResponseCallback callback) {
		List<NameValuePair> nameValuePairs = parameterMaker.makeParameters(map);
		client.post(url).addParams(nameValuePairs).execute(callback);
	}

	@Override
	public void delete(String url, IResponseCallback callback) {
		client.delete(url).execute(callback);
	}

	@Override
	public void getDepth(String url, int depth, final ICallback<Map<String, Object>> callback) {
		client.get(url + "." + depth + ".json").execute(new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				try {
					String string = response.asString();
					Map<String, Object> result = parameterMaker.makeFrom(string);
					callback.process(result);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
	}

	@Override
	public void postMany(String parentUrl, Map<String, Object> map, IResponseCallback callback) {
		client.post(parentUrl).//
				addParams(//
						RepositoryCacheConstants.operation, RepositoryCacheConstants.importOperation,//
						RepositoryCacheConstants.contentType, RepositoryCacheConstants.json, //
						RepositoryCacheConstants.content, Json.toString(map) //
				).//
				execute(callback);

	}
}
