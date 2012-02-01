package org.softwareFm.client.http.requests;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.softwareFm.client.http.response.IResponse;

public class CheckCallback implements IResponseCallback {

	private final int status;
	private final String message;
	private final AtomicInteger count = new AtomicInteger();
	private final AtomicInteger succeeded = new AtomicInteger();

	public CheckCallback(int status, String message) {
		this.status = status;
		this.message = message;
	}

	@Override
	public void process(IResponse response) {
		count.incrementAndGet();
		Assert.assertEquals(message, response.asString());
		Assert.assertEquals(status, response.statusCode());
		succeeded.incrementAndGet();
	}

	public void assertCalledSuccessfullyOnce() {
		Assert.assertEquals(1, count.get());
		Assert.assertEquals(1, succeeded.get());
	}

}
