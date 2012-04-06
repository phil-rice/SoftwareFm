package org.softwareFm.crowdsource.utilities.transaction;

import java.util.concurrent.CountDownLatch;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;

public class CallbackWithKick<T> implements ICallback<T> {
	private final CountDownLatch latch = new CountDownLatch(1);

	public void kick() {
		latch.countDown();
	}

	@Override
	public void process(T t) throws Exception {
		latch.await();
	}

}