package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class DeleteProcessor implements IProcessCall {

	private final IGitServer gitServer;

	public DeleteProcessor(IGitServer gitServer) {
		this.gitServer = gitServer;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(ServerConstants.DELETE)) {
			String uri = requestLine.getUri();
			gitServer.delete(uri);
			return IProcessResult.Utils.doNothing();
		}
		return null;
	}

}
