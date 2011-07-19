package org.arc4eclipse.repositoryFacard.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;

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
		RepositoryFrontEndTest.assertTrue("Code: " + code + "\n" + responseString.get(), code == 200 || code == 201);
	}
}