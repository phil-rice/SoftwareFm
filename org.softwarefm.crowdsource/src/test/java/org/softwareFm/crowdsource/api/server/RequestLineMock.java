package org.softwareFm.crowdsource.api.server;

import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;

public class RequestLineMock implements RequestLine {

	private final String method;
	private final String uri;

	public RequestLineMock(String method, String uri) {
		this.method = method;
		this.uri = uri;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public String toString() {
		return "RequestLineMock [method=" + method + ", uri=" + uri + "]";
	}

}