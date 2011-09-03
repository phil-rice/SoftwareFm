package org.softwareFm.repositoryFacard.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;

class ResponseCallbackRecordingStatus implements IResponseCallback {
	public AtomicInteger statusCode = new AtomicInteger();
	public AtomicReference<String> responseString = new AtomicReference<String>();

	@Override
	public void process(IResponse response) {
		statusCode.set(response.statusCode());
		responseString.set(response.asString());
	}

	public void assertOk() {
		int code = statusCode.get();
		RepositoryFacardTest.assertTrue("Code: " + code + "\n" + responseString.get(), code == 200 || code == 201);
	}
}