package org.softwareFm.server.processors;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ServerConstants;

public class DeleteProcessor implements IProcessCall {

	private final IGitServer gitServer;

	public DeleteProcessor(IGitServer gitServer) {
		this.gitServer = gitServer;
	}

	@Override
	public String process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(ServerConstants.DELETE)) {
			String uri = requestLine.getUri();
			gitServer.delete(uri);		}
		return null;
	}

}
