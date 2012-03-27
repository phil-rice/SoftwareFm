package org.softwareFm.crowdsource.utilities.callbacks;

import java.util.List;

import org.softwareFm.crowdsource.utilities.collections.Lists;

public class MemoryWithThreadCallback<T> extends MemoryCallback<T> {

	public final List<Thread> threads = Lists.newList();

	@Override
	public void process(T t) throws Exception {
		super.process(t);
		threads.add(Thread.currentThread());
	}
public 
	Thread getOnlyThread() {
		return Lists.getOnly(threads);
	}

}
