package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IFileDescription;
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
			IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, parameters);
			gitServer.delete(fileDescription);
			return IProcessResult.Utils.doNothing();
		}
		return null;
	}

}
