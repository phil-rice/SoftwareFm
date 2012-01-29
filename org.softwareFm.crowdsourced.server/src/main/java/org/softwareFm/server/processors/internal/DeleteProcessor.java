package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class DeleteProcessor implements IProcessCall {


	private final IGitOperations gitOperations;

	public DeleteProcessor(IGitOperations gitOperations) {
		this.gitOperations = gitOperations;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(CommonConstants.DELETE)) {
			String uri = requestLine.getUri();
			gitOperations.addAllAndCommit(uri, "delete: "+ uri);
			return IProcessResult.Utils.doNothing();
		}
		return null;
	}

}
