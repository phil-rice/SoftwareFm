package org.arc4eclipse.httpClient.response;

public interface IResponse {

	String url();

	int statusCode();

	String asString();

}
