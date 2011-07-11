package org.arc4eclipse.httpClient.requests;

import java.util.concurrent.atomic.AtomicInteger;

import org.arc4eclipse.httpClient.response.IResponse;

public class MemoryResponseCallback<Thing, Aspect> implements IResponseCallback<Thing, Aspect> {

	public Thing thing;
	public Aspect aspect;
	public IResponse response;
	public AtomicInteger count = new AtomicInteger();

	
	public <T> void process(Thing thing, Aspect aspect, IResponse response) {
		this.thing = thing;
		this.aspect = aspect;
		this.response = response;
		count.incrementAndGet();
	}

}
