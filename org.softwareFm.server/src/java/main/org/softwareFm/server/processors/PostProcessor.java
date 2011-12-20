package org.softwareFm.server.processors;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class PostProcessor implements IProcessCall {

	private final IGitServer server;

	public PostProcessor(IGitServer server) {
		this.server = server;
	}

	@Override
	public String process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(ServerConstants.POST)) {
			Object data = parameters.get(ServerConstants.dataParameterName);
			if (data instanceof String) {
				String url = requestLine.getUri();
				GetResult initial = server.getFile(url);
				String newData = initial.found ? merge(initial, (String) data) : (String) data;
				server.post(url, Json.mapFromString(newData));
			} else
				throw new IllegalArgumentException(data.getClass() + "\n" + data);
			return "";
		}
		return null;
	}

	private String merge(GetResult initial, String data) {
		Map<String, Object> initialData = initial.data;
		Map<String, Object> newMap = Json.mapFromString(data);
		@SuppressWarnings("unchecked")
		Map<String, Object> composite = Maps.<String,Object>merge(initialData, newMap);
		return Json.toString(composite);
	}
}
