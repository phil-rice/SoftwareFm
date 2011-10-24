package org.softwareFm.repositoryFacard.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IAspectToParameters;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacardConstants.RepositoryFacardConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.json.Json;

public class RepositoryFacard implements IRepositoryFacard {

	private final IHttpClient client;
	private final IAspectToParameters parameterMaker;
	private final List<NameValuePair> headers = Lists.newList();
	private final String getExtension;

	public RepositoryFacard(IHttpClient client, String getExtension) {
		this(client, getExtension, new AspectToParameters());
	}

	public RepositoryFacard(IHttpClient client, String getExtension, IAspectToParameters parameters) {
		this.client = client;
		this.parameterMaker = parameters;
		this.getExtension = "." + getExtension;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Future<Map<String, Object>> get(String url, final IRepositoryFacardCallback callback) {
		Future<?> future = client.get(url + getExtension).execute(new IResponseCallback() {
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
		return (Future<Map<String, Object>>) future;
	}

	@Override
	public Future<?> post(String url, Map<String, Object> map, IResponseCallback callback) {
		// System.out.println(url+" <--------- " + map);
		// return Futures.doneFuture(null);
		List<NameValuePair> nameValuePairs = parameterMaker.makeParameters(map);
		return client.post(url).addParams(nameValuePairs).execute(callback);
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
	public Future<?> delete(String url, IResponseCallback callback) {
		return client.delete(url).execute(callback);
	}

	@Override
	public Future<?> getDepth(String url, int depth, final IRepositoryFacardCallback callback) {
		return client.get(url + "." + depth + getExtension).execute(new IResponseCallback() {
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
	public void shutdown() {
		client.shutdown();
	}

	@Override
	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}
}
