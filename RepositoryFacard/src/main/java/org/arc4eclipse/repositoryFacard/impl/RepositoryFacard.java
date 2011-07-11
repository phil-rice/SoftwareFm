package org.arc4eclipse.repositoryFacard.impl;

import java.util.HashMap;
import java.util.Map;

import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.IResponse;
import org.arc4eclipse.repositoryClient.api.IRepositoryClient;
import org.arc4eclipse.repositoryFacard.IAspectToParameters;
import org.arc4eclipse.repositoryFacard.IRepositoryFacard;
import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.utilities.maps.Maps;

//TODO This is memory leak city. A LRU cache or something else will help a lot
public class RepositoryFacard<Key, Thing, Aspect, Data> implements IRepositoryFacard<Key, Thing, Aspect, Data> {

	IRepositoryClient<Thing, Aspect> delegate;
	private final Map<Key, Map<Aspect, Data>> cache = Maps.newMap();
	private final IAspectToParameters<Thing, Aspect, Data> aspectToParameters;

	public RepositoryFacard(IRepositoryClient<Thing, Aspect> delegate, IAspectToParameters<Thing, Aspect, Data> aspectToParameters) {
		this.delegate = delegate;
		this.aspectToParameters = aspectToParameters;
	}

	
	public void getDetails(final Key key, Thing thing, Aspect aspect, final IRepositoryFacardCallback<Key, Thing, Aspect, Data> callback) {
		if (cache.containsKey(key)) {
			Map<Aspect, Data> map = cache.get(key);
			if (map.containsKey(aspect)) {
				callback.process(key, thing, aspect, map.get(aspect));
				return;
			}
		}
		delegate.getDetails(thing, aspect, new IResponseCallback<Thing, Aspect>() {
			
			public <T> void process(Thing thing, Aspect aspect, IResponse response) {
				Data data = (response.statusCode() == 200) ? aspectToParameters.makeFrom(response.asString()) : aspectToParameters.makeEmpty();
				Maps.addToMapOfMaps(cache, HashMap.class, key, aspect, data);
				callback.process(key, thing, aspect, data);
			}
		});
	}

	
	public void setDetails(final Key key, Thing thing, Aspect aspect, final Data data, final IRepositoryFacardCallback<Key, Thing, Aspect, Data> callback) {
		String[] parameters = aspectToParameters.makeParameters(thing, aspect, data);
		delegate.setDetails(thing, aspect, new IResponseCallback<Thing, Aspect>() {
			
			public <T> void process(Thing thing, Aspect aspect, IResponse response) {
				callback.process(key, thing, aspect, data);
			}
		}, parameters);
	}

}
