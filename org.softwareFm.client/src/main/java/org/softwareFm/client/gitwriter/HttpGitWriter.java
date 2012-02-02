package org.softwareFm.client.gitwriter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitWriter;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.url.Urls;

public class HttpGitWriter implements IGitWriter {

	private final IHttpClient httpClient;
	private final long delayMs;

	public HttpGitWriter(IHttpClient httpClient) {
		this (httpClient, CommonConstants.clientTimeOut);
	}
	public HttpGitWriter(IHttpClient httpClient, long delayMs) {
		this.httpClient = httpClient;
		this.delayMs = delayMs;
	}

	@Override
	public void init(String url) {
		try {
			httpClient.post(Urls.compose(CommonConstants.makeRootPrefix, url)).execute(IResponseCallback.Utils.noCallback()).get(delayMs, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void put(IFileDescription fileDescription, Map<String, Object> data) {
		try {
			httpClient.post(fileDescription.url()).//
					addParam(CommonConstants.dataParameterName, Json.toString(data)).//
					execute(IResponseCallback.Utils.noCallback()).//
					get(delayMs, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void delete(IFileDescription fileDescription) {
		try {
			httpClient.delete(fileDescription.url()).execute(IResponseCallback.Utils.noCallback()).get(delayMs, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
