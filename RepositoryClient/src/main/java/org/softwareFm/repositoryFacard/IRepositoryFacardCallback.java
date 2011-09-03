package org.softwareFm.repositoryFacard;

import java.util.Map;

import org.softwareFm.httpClient.response.IResponse;

public interface IRepositoryFacardCallback {

	void process(IResponse response, Map<String, Object> data);
}
