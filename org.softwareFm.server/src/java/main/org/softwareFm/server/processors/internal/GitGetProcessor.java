package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.RequestLine;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.maps.UrlCache;

public class GitGetProcessor implements IProcessCall {
	private final IGitServer server;
	private final UrlCache<String> cache;

	public GitGetProcessor(IGitServer server, UrlCache<String> cache) {
		this.server = server;
		this.cache = cache;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (ServerConstants.GET.equals(requestLine.getMethod())) {
			return IProcessResult.Utils.processString(getString(requestLine));
		}
		return null;
	}

	private String getString(RequestLine requestLine) {
		final String url = requestLine.getUri();
		String existing = cache.get(url);
		if (existing != null)
			return existing;
		File repositoryLocation = server.findRepositoryUrl(url);
		if (repositoryLocation == null) {
			return cache.findOrCreate(url, new Callable<String>() {
				@Override
				public String call() throws Exception {
					GetResult data = server.localGet(url);
					return Json.toString(Maps.stringObjectLinkedMap(ServerConstants.dataKey, data.data));
				}
			});
		} else {
			String repoUrl = Files.offset(server.getRoot(), repositoryLocation);
			return Json.toString(Maps.stringObjectLinkedMap(ServerConstants.repoUrlKey, repoUrl));
		}
	}

}
