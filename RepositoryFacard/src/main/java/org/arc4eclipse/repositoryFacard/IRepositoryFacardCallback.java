package org.arc4eclipse.repositoryFacard;

import java.util.Map;

import org.arc4eclipse.httpClient.response.IResponse;

public interface IRepositoryFacardCallback {

	void process(IResponse response, Map<String, Object> data);
}
