package org.arc4eclipse.repositoryClient.api.impl;

import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.repositoryClient.api.IRepositoryClient;
import org.arc4eclipse.repositoryClient.paths.IPathCalculator;

public class RepositoryClient<Thing, Aspect> implements IRepositoryClient<Thing, Aspect> {

	private final IPathCalculator<Thing, Aspect> pathCalculator;
	private final IHttpClient client;

	public RepositoryClient(IPathCalculator<Thing, Aspect> pathCalculator, IHttpClient client) {
		this.pathCalculator = pathCalculator;
		this.client = client;
	}

	public void getDetails(Thing thing, Aspect aspect, IResponseCallback<Thing, Aspect> callback) {
		String url = pathCalculator.makeUrl(thing, aspect);
		if (url != null)
			client.get(url + ".json").execute(thing, aspect, callback);
	}

	public void setDetails(Thing thing, Aspect aspect, IResponseCallback<Thing, Aspect> callback, String... details) {
		String url = pathCalculator.makeUrl(thing, aspect);
		if (url != null)
			client.post(url).addParams(details).execute(thing, aspect, callback);
	}

	
	public String toString() {
		return "RepositoryClient [pathCalculator=" + pathCalculator + ", client=" + client + "]";
	}

}
