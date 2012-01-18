package org.softwareFm.httpClient.requests;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;

public class CheckMapCallback implements IResponseCallback {

	private final int status;
	private final AtomicInteger count = new AtomicInteger();
	private final AtomicInteger succeeded = new AtomicInteger();
	private final Map<String, Object> expected;

	public CheckMapCallback(int status, Object...namesAndValues) {
		this.status = status;
		this.expected = Maps.stringObjectLinkedMap(namesAndValues);
	}

	@Override
	public void process(IResponse response) {
		count.incrementAndGet();
		Assert.assertEquals(expected, Json.mapFromString(response.asString()));
		Assert.assertEquals(status, response.statusCode());
		succeeded.incrementAndGet();
	}

	public void assertCalledSuccessfullyOnce() {
		Assert.assertEquals(1, count.get());
		Assert.assertEquals(1, succeeded.get());
	}

}
