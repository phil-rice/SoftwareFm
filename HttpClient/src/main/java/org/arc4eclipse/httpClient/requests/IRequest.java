package org.arc4eclipse.httpClient.requests;

import java.util.concurrent.Future;

public interface IRequest {

	String getUrl();

	Future<?> execute(IResponseCallback callback);

}
