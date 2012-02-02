package org.softwareFm.server.processors.internal;

import java.io.InputStream;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class FavIconProcessor implements IProcessCall {

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (CommonConstants.GET.equals(requestLine.getMethod())) {
			String lastSegment = Strings.lastSegment(requestLine.getUri(), "/");
			if ("favicon.ico".equals(lastSegment)) {
				InputStream stream = getClass().getClassLoader().getResourceAsStream("sfmLogo.ico");
				return IProcessResult.Utils.processStream(stream, "image/vnd.microsoft.icon");
			}
		}
		return null;
	}

}
