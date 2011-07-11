package org.arc4eclipse.repositoryFacard;


public interface IRepositoryFacardCallback<Key, Thing, Aspect, Data> {

	void process(Key key, Thing thing, Aspect aspect, Data data);
}
