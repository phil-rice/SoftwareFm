package org.softwarefm.httpServer.routes;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.utilities.http.HttpMethod;

public class RouteHandlerMock implements IRouteHandler {
	public HttpMethod method;
	public Map<String, String> parameters;
	public HttpEntity entity;
	public AtomicInteger count = new AtomicInteger();

	private Throwable throwable;
	public final String responseString;
	public final int statusCode;

	public RouteHandlerMock(String responseString, int statusCode) {
		this.responseString = responseString;
		this.statusCode = statusCode;
	}

	@Override
	public StatusAndEntity execute(HttpMethod method, Map<String, String> parameters, HttpEntity entity) throws Exception {
		this.count.incrementAndGet();
		this.method = method;
		this.parameters = parameters;
		this.entity = entity;
		if (throwable != null)
			if (throwable instanceof Exception)
				throw (Exception) throwable;
			else if (throwable instanceof Error)
				throw (Error) throwable;
			else
				throw new IllegalStateException("Don't know how to throw: " + throwable.getClass().getName());
		return new StatusAndEntity(statusCode, new StringEntity(responseString), false);
	}
	
	public int getCount() {
		return count.get();
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

}