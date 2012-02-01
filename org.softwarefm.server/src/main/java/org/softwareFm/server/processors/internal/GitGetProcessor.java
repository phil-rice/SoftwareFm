package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.RequestLine;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.maps.UrlCache;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class GitGetProcessor implements IProcessCall {
	private final IGitOperations gitOperations;
	private final UrlCache<String> cache;

	public GitGetProcessor(IGitOperations gitOperations, UrlCache<String> cache) {
		this.gitOperations = gitOperations;
		this.cache = cache;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (CommonConstants.GET.equals(requestLine.getMethod())) {
			return IProcessResult.Utils.processString(getString(requestLine));
		}
		return null;
	}

	private String getString(final RequestLine requestLine) {
		IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, Maps.emptyStringObjectMap());
		final String url = requestLine.getUri();
		String existing = cache.get(url);
		if (existing != null)
			return existing;
		File root = gitOperations.getRoot();
		File repositoryLocation = fileDescription.findRepositoryUrl(root);
		if (repositoryLocation == null) {
			return cache.findOrCreate(url, new Callable<String>() {
				@Override
				public String call() throws Exception {
					IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, Maps.emptyStringObjectMap());
					Map<String, Object> data = gitOperations.getFileAndDescendants(fileDescription);
					return Json.mapToString(CommonConstants.dataKey, data);
				}
			});
		} else {
			String repoUrl = Files.offset(root, repositoryLocation);
			return Json.toString(Maps.stringObjectLinkedMap(CommonConstants.repoUrlKey, repoUrl));
		}
	}

}
