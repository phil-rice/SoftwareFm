package org.softwareFm.server.processors;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.json.Json;

public class PostProcessor implements IProcessCall {

	private final IGitServer server;

	public PostProcessor(IGitServer server) {
		this.server = server;
	}

	@Override
	public String process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(ServerConstants.POST)) {
			Object data = parameters.get(ServerConstants.dataParameterName);
			if (data instanceof String)
				server.post(requestLine.getUri(), Json.mapFromString((String) data));
			else 
				throw new IllegalArgumentException(data.getClass() + "\n" + data);
			return "";
		}
		return null;
	}
}
