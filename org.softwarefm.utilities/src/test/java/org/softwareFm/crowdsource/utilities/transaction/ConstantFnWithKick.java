package org.softwareFm.crowdsource.utilities.transaction;

import java.util.concurrent.CountDownLatch;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class ConstantFnWithKick<From, To> implements IFunction1<From, To> {
	private final To result;
	private final CountDownLatch latch = new CountDownLatch(1);

	public ConstantFnWithKick(To result) {
		super();
		this.result = result;
	}

	@Override
	public To apply(From from) throws Exception {
		latch.await();
		return result;
	}

	public void kick() {
		latch.countDown();
	}

}