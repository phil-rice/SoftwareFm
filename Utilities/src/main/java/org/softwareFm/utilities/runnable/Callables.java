package org.softwareFm.utilities.runnable;

import java.util.concurrent.Callable;

public class Callables {

	public static Callable<Long> time() {
		return new Callable<Long>() {
			@Override
			public Long call() throws Exception {
				return System.currentTimeMillis();
			}
		};
	}

}
