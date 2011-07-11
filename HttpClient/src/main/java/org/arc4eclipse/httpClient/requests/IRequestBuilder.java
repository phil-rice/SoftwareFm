package org.arc4eclipse.httpClient.requests;


public interface IRequestBuilder extends IRequest {

	IRequestBuilder addParam(String name, String value);

	IRequestBuilder addParams(String... nameAndValue);
}