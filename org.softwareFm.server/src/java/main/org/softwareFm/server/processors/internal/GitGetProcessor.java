package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class GitGetProcessor implements IProcessCall {
	private final IGitServer server;

	public GitGetProcessor(IGitServer server) {
		this.server = server;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (ServerConstants.GET.equals(requestLine.getMethod())) {
			return IProcessResult.Utils.processString(getString(requestLine));
		}
		return null;
	}

	private String getString(RequestLine requestLine) {
		String url = requestLine.getUri();
		File repositoryLocation = server.findRepositoryUrl(url);
		if (repositoryLocation == null) {
			GetResult data = server.localGet(url);
			return Json.toString(Maps.stringObjectLinkedMap(ServerConstants.dataKey, data.data));
		} else {
			String repoUrl = Files.offset(server.getRoot(), repositoryLocation);
			return Json.toString(Maps.stringObjectLinkedMap(ServerConstants.repoUrlKey, repoUrl));
		}
	}

}
