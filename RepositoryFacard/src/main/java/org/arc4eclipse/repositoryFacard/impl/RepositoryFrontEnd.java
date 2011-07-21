package org.arc4eclipse.repositoryFacard.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.http.NameValuePair;
import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.repositoryFacard.IAspectToParameters;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.repositoryFacardConstants.RepositoryFacardConstants;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.json.Json;

public class RepositoryFrontEnd implements IRepositoryFacard {

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
	public Future<?> get(String url, final IRepositoryFacardCallback callback) {
		return client.get(url + ".json").execute(new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				try {
					if (RepositoryFacardConstants.okStatusCodes.contains(response.statusCode())) {
						String string = response.asString();
						Map<String, Object> result = parameterMaker.makeFrom(string);
						callback.process(response, result);
					} else
						callback.process(response, null);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
	}

	@Override
	public Future<?> post(String url, Map<String, Object> map, IResponseCallback callback) {
		List<NameValuePair> nameValuePairs = parameterMaker.makeParameters(map);
		return client.post(url).addParams(nameValuePairs).execute(callback);
	}

	@Override
	public Future<?> delete(String url, IResponseCallback callback) {
		return client.delete(url).execute(callback);
	}

	@Override
	public Future<?> getDepth(String url, int depth, final IRepositoryFacardCallback callback) {
		return client.get(url + "." + depth + ".json").execute(new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				try {
					String string = response.asString();
					Map<String, Object> result = parameterMaker.makeFrom(string);
					callback.process(response, result);
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}
		});
	}

	@Override
	public Future<?> postMany(String parentUrl, Map<String, Object> map, IResponseCallback callback) {
		return client.post(parentUrl).//
				addParams(//
						RepositoryFacardConstants.operation, RepositoryFacardConstants.importOperation,//
						RepositoryFacardConstants.contentType, RepositoryFacardConstants.json, //
						RepositoryFacardConstants.content, Json.toString(map) //
				).//
				execute(callback);

	}

	@Override
	public void shutdown() {
		client.shutdown();
	}
}
