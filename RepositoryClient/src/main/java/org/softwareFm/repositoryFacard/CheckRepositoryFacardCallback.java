package org.softwareFm.repositoryFacard;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.softwareFm.httpClient.response.IResponse;

public class CheckRepositoryFacardCallback implements IRepositoryFacardCallback {
	private final Map<String, Object> expected;
	private final int statusCode;
	private final AtomicInteger count = new AtomicInteger();

	public CheckRepositoryFacardCallback(Map<String, Object> expected, int statusCode) {
		this.expected = expected;
		this.statusCode = statusCode;
	}

	@Override
	public void process(IResponse response, Map<String, Object> data) throws Exception {
		if (count.getAndIncrement() > 0)
			Assert.fail();
		Assert.assertEquals(statusCode, response.statusCode());
		Assert.assertEquals(expected, data);
	}

	public void assertCalled() {
		Assert.assertEquals(1, count.get());
	}
}