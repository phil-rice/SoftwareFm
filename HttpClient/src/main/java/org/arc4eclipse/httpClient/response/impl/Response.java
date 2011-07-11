package org.arc4eclipse.httpClient.response.impl;

import org.arc4eclipse.httpClient.response.IResponse;

public class Response implements IResponse {

	private final int statusCode;
	private final String string;

	public Response(int statusCode, String string) {
		this.statusCode = statusCode;
		this.string = string;
	}

	
	public int statusCode() {
		return statusCode;
	}

	
	public String asString() {
		return string;
	}

	
	public String toString() {
		return "Response [statusCode=" + statusCode + ", string=" + string + "]";
	}

}
