package org.arc4eclipse.repositoryFacard.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.impl.Response;
import org.arc4eclipse.repositoryClient.api.IRepositoryClient;
import org.arc4eclipse.repositoryFacard.constants.RepositoryFacardConstants;

public class RepositoryClientMock<Thing, Aspect> implements IRepositoryClient<Thing, Aspect> {

	public Thing thing;
	public Aspect aspect;
	private int statusCode;
	public final AtomicInteger putCount = new AtomicInteger();
	public final AtomicInteger getCount = new AtomicInteger();

	public RepositoryClientMock() {
		this(200);
	}

	public RepositoryClientMock(int statusCode) {
		this.statusCode = statusCode;
	}

	
	public void getDetails(Thing thing, Aspect aspect, IResponseCallback<Thing, Aspect> callback) {
		this.thing = thing;
		this.aspect = aspect;
		getCount.incrementAndGet();
		callback.process(thing, aspect, new Response(statusCode, makeString()));
	}

	
	public void setDetails(Thing thing, Aspect aspect, IResponseCallback<Thing, Aspect> callback, String... details) {
		this.thing = thing;
		this.aspect = aspect;
		putCount.incrementAndGet();
		callback.process(thing, aspect, new Response(statusCode, makeString()));

	}

	private String makeString() {
		return "{\"" + RepositoryFacardConstants.dataKey + "\":{\"put\":" + putCount.get() + ",\"get\":" + getCount.get() + "}}";
	}
}
