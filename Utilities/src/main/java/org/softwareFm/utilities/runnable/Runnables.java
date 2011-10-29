package org.softwareFm.utilities.runnable;

import java.util.concurrent.atomic.AtomicInteger;

public class Runnables {

	public static  class CountRunnable implements Runnable {
		private final AtomicInteger count = new AtomicInteger();

		@Override
		public void run() {
			count.incrementAndGet();
			detail();
		}

		protected void detail() {
		}

		public int getCount() {
			return count.get();
		}
	}

	public static Runnable noRunnable = new Runnable() {
		@Override
		public void run() {
		}
	};

	public static CountRunnable count() {
		return new CountRunnable();
	}

}
