package org.arc4eclipse.repositoryFacard.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;

public class MemoryRepositoryFacardGetCallback<Key, Thing, Aspect, Data> implements IRepositoryFacardCallback<Key, Thing, Aspect, Data> {

	public Key key;
	public Thing thing;
	public Aspect aspect;
	public Data data;
	public final AtomicInteger count = new AtomicInteger();

	public void process(Key key, Thing thing, Aspect aspect, Data data) {
		this.key = key;
		this.thing = thing;
		this.aspect = aspect;
		this.data = data;
		this.count.incrementAndGet();
	}
}