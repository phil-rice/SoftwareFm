package org.softwareFm.httpClient.response.impl;

import org.softwareFm.httpClient.response.IResponse;

public class Response implements IResponse {

	private final int statusCode;
	private final String string;
	private final String url;

	public Response(String url, int statusCode, String string) {
		this.url = url;
		this.statusCode = statusCode;
		this.string = string;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public int statusCode() {
		return statusCode;
	}

	@Override
	public String asString() {
		return string;
	}

	@Override
	public String toString() {
		return "Response [url=" + url + ", statusCode=" + statusCode + ", string=" + string + "]";
	}

}
