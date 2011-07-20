package org.arc4eclipse.httpClient.requests;

public interface IRequest {

	String getUrl();

	void execute(IResponseCallback callback);

}
