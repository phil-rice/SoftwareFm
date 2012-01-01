package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class GetIndexProcessor implements IProcessCall {

	private final File root;

	public GetIndexProcessor(File root) {
		super();
		this.root = root;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (ServerConstants.GET.equals(requestLine.getMethod()) && requestLine.getUri().equals("/")) {
			File file = new File(root, "index.html");
			return IProcessResult.Utils.processFile(file);

		}
		return null;
	}

}
