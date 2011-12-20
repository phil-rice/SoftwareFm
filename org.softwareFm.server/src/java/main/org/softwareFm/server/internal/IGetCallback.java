package org.softwareFm.server.internal;

import java.util.Map;

import org.softwareFm.server.GetResult;

public interface IGetCallback {

	GetResult aboveRepositoryData(Map<String, Object> data);

	GetResult repositoryBase(String repositoryBase);

	void invalidResponse(int statusCode, String message);
}
