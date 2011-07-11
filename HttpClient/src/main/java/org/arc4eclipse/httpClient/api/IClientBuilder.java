package org.arc4eclipse.httpClient.api;


public interface IClientBuilder extends IHttpClient {

	IClientBuilder withCredentials(String userName, String password);

}
