package org.softwareFm.gitwriter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.requests.MemoryResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.server.IRepoFinder;
import org.softwareFm.server.RepoDetails;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.strings.Strings;

public class HttpRepoFinder implements IRepoFinder {

	private final IHttpClient client;
	private final long timeOutInMs;

	public HttpRepoFinder(IHttpClient client, long timeOutInMs) {
		this.client = client;
		this.timeOutInMs = timeOutInMs;
	}

	@Override
	public void clearCaches() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public RepoDetails findRepoUrl(String url) {
		try {
			MemoryResponseCallback memoryCallback = IResponseCallback.Utils.memoryCallback();
			client.get(url).execute(memoryCallback).get(timeOutInMs, TimeUnit.MILLISECONDS);
			IResponse response = memoryCallback.response;
			if (CommonConstants.okStatusCodes.contains(response.statusCode())) {
				Map<String, Object> data = Json.mapFromString(response.asString());
				if (data.containsKey(CommonConstants.repoUrlKey))
					return RepoDetails.repositoryUrl(Strings.nullSafeToString(data.get(CommonConstants.repoUrlKey)));
				if (!data.containsKey(CommonConstants.dataParameterName))
					throw new IllegalStateException(data.toString());
				return RepoDetails.aboveRepo((Map<String, Object>) data.get(CommonConstants.dataKey));
			} else
				throw new IllegalStateException(response.toString());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void clearCache(String url) {
	}
}
