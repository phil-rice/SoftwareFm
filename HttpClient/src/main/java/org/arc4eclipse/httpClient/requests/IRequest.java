package org.arc4eclipse.httpClient.requests;

public interface IRequest {

	<Thing, Aspect> void execute(Thing context1, Aspect context2, IResponseCallback<Thing, Aspect> callback);

}
