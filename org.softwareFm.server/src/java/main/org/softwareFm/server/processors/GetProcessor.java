package org.softwareFm.server.processors;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class GetProcessor implements IProcessCall {
	private final IGitServer server;

	public GetProcessor(IGitServer server) {
		this.server = server;
	}

	@Override
	public String process(RequestLine requestLine, Map<String, Object> parameters) {
		if (ServerConstants.GET.equals(requestLine.getMethod())) {
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
		return null;
	}

}
