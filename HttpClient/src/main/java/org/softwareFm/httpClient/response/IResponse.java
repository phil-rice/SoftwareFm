package org.softwareFm.httpClient.response;

public interface IResponse {

	String url();

	int statusCode();

	String asString();

}
