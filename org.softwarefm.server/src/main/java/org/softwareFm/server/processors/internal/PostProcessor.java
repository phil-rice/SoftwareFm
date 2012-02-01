package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.json.Json;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class PostProcessor implements IProcessCall {

	private final IGitOperations gitOperations;

	public PostProcessor(IGitOperations gitOperations) {
		this.gitOperations = gitOperations;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(CommonConstants.POST)) {
			Object data = parameters.get(CommonConstants.dataParameterName);
			if (data instanceof String) {
				IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, parameters);
				Map<String,Object> actualData = Json.mapFromString((String) parameters.get(CommonConstants.dataParameterName));
				IFileDescription.Utils.merge(gitOperations, fileDescription,  actualData);
			} else
				throw new IllegalArgumentException(data.getClass() + "\n" + data);
			return IProcessResult.Utils.doNothing();
		}
		return null;
	}


}
