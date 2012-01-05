package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class HeadThrowing404 implements IProcessCall {


	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (ServerConstants.HEAD.equals(requestLine.getMethod())) {
				return IProcessResult.Utils.processError(ServerConstants.notFoundStatusCode, ServerConstants.notFoundMessage);
		}
		return null;
	}

}
