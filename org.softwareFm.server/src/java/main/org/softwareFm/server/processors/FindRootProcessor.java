package org.softwareFm.server.processors;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.utilities.collections.Files;

public class FindRootProcessor implements IProcessCall {

	private final IGitServer server;

	public FindRootProcessor(IGitServer server) {
		this.server = server;
	}

	@Override
	public String process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(ServerConstants.GET)) {
			String url = requestLine.getUri(); // need to trim this a bit!
			if (url.substring(1).startsWith(ServerConstants.findRepositoryBasePrefix)) {
				String actualUrl = url.substring(ServerConstants.findRepositoryBasePrefix.length() + 1);
				File repositoryLocation = server.findRepositoryUrl(actualUrl);
				String result = Files.offset(server.getRoot(), repositoryLocation);
				return result;
			}
		}
		return null;
	}

}
