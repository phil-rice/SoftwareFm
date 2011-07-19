package org.arc4eclipse.httpClient.requests;

import java.util.concurrent.atomic.AtomicInteger;

import org.arc4eclipse.httpClient.response.IResponse;

public class MemoryResponseCallback<Thing, Aspect> implements IResponseCallback {

	public IResponse response;
	public AtomicInteger count = new AtomicInteger();

	@Override
	public void process(IResponse response) {
		this.response = response;
		count.incrementAndGet();
	}

}
