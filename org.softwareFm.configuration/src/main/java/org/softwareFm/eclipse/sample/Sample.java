package org.softwareFm.eclipse.sample;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.configuration.fixture.SoftwareFmFixture;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;

public class Sample {

	public static void main(String[] args) {
		final ExecutorService service = new ThreadPoolExecutor(2, 10, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
		try {
			Swts.display("Sample", new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					return new SoftwareFmFixture(from.getDisplay(), service).makeComposite(from).getComposite();
				}
			});
		} finally {
			service.shutdown();
		}
	}
}
