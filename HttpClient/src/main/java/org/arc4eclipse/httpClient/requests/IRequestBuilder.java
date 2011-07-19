package org.arc4eclipse.httpClient.requests;

import java.util.List;

import org.apache.http.NameValuePair;

public interface IRequestBuilder extends IRequest {

	IRequestBuilder addParams(List<NameValuePair> nameAndValues);

	IRequestBuilder addParam(String name, String value);

	IRequestBuilder addParams(String... nameAndValue);
}