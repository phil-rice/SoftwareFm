package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.CommonMessages;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class HeadThrowing404 implements IProcessCall {

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (CommonConstants.HEAD.equals(requestLine.getMethod())) {
			return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, CommonMessages.notFoundMessage);
		}
		return null;
	}

}
