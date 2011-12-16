package org.softwareFm.server.processors;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ServerConstants;

public class PostProcessor implements IProcessCall{

	private final IGitServer server;
	
	public PostProcessor(IGitServer server) {
		this.server = server;
	}

	@Override
	public String process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(ServerConstants.POST)){
			server.post(requestLine.getUri(), parameters);
			return "";
		}
		return null;
	}

}
