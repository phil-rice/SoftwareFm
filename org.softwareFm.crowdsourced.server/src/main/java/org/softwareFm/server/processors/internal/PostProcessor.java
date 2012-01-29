package org.softwareFm.server.processors.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class PostProcessor implements IProcessCall {

	private final IGitOperations gitOperations;

	public PostProcessor(IGitOperations gitOperations) {
		this.gitOperations = gitOperations;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(CommonConstants.POST)) {
			Object data = parameters.get(CommonConstants.dataParameterName);
			if (data instanceof String) {
				IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, parameters);
				Map<String, Object> newData = Json.mapFromString(data.toString());
				Map<String, Object> initialData = gitOperations.getFile(fileDescription);
				Map<String, Object> map = Maps.merge(initialData, newData);
				gitOperations.put(fileDescription, map);
			} else
				throw new IllegalArgumentException(data.getClass() + "\n" + data);
			return IProcessResult.Utils.doNothing();
		}
		return null;
	}

}
