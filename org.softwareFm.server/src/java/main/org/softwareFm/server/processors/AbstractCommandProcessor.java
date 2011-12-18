package org.softwareFm.server.processors;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.IProcessCall;

public abstract class AbstractCommandProcessor implements IProcessCall {

	protected final IGitServer server;
	private final String method;
	private final String prefix;

	public AbstractCommandProcessor(IGitServer server, String method, String prefix) {
		this.server = server;
		this.method = method;
		this.prefix = prefix;
	}

	@Override
	public String process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(method)) {
			String url = requestLine.getUri(); // need to trim this a bit!
			if (url.substring(1).startsWith(prefix)) {
				String actualUrl = url.substring(prefix.length() + 1);
				return execute(actualUrl);
			}
		}
		return null;
	}

	abstract protected String execute(String actualUrl);

}
