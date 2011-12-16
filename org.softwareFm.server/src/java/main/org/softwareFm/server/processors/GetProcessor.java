package org.softwareFm.server.processors;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.json.Json;

public class GetProcessor implements IProcessCall{

	private final IGitServer server;
	
	public GetProcessor(IGitServer server) {
		this.server = server;
	}

	@Override
	public String process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(ServerConstants.GET)){
			GetResult result = server.get(requestLine.getUri());
			return Json.toString(result.data);
		}
		return null;
	}

}
