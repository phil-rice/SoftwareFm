package org.softwareFm.repositoryFacard.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;

final class ResponseFacardCallbackRecordingStatus implements IRepositoryFacardCallback {
	public AtomicInteger statusCode = new AtomicInteger();
	public AtomicReference<IResponse> response = new AtomicReference<IResponse>();
	public AtomicReference<Map<String, Object>> data = new AtomicReference<Map<String, Object>>();
	public AtomicInteger count = new AtomicInteger();

	@Override
	public void process(IResponse response, Map<String, Object> data) {
		statusCode.set(response.statusCode());
		this.response.set(response);
		this.data.set(data);
		if (count.incrementAndGet() > 1)
			RepositoryFacardTest.fail();

	}

	public void assertOk() {
		int code = statusCode.get();
		RepositoryFacardTest.assertTrue("Code: " + code + "\n" + response.get(), code == 200 || code == 201);
	}

}