package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public abstract class AbstractCommandProcessor implements IProcessCall {

	protected IGitOperations gitOperations;
	private final String method;
	private final String prefix;

	public AbstractCommandProcessor(IGitOperations gitOperations, String method, String prefix) {
		this.gitOperations = gitOperations;
		this.method = method;
		this.prefix = prefix;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(method)) {
			String url = requestLine.getUri(); // need to trim this a bit!
			if (url.substring(1).startsWith(prefix)) {
				String actualUrl = url.substring(prefix.length() + 1);
				return execute(actualUrl, parameters);
			}
		}
		return null;
	}

	abstract protected IProcessResult execute(String actualUrl, Map<String, Object> parameters);

}
