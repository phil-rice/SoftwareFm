/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.loadtest;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.profiling.Stats;
import org.softwareFm.common.services.IServiceExecutor;

public class MultiThreadedTimeTester<Context> {

	private final ITimable<Context> toBeTested;
	private final IServiceExecutor serviceExecutor;
	private final int threads;
	private final int timePerThread;
	private final Map<Integer, Stats> times = Maps.newMap();

	public MultiThreadedTimeTester(int threads, int timePerThread, ITimable<Context> toBeTested) {
		this.threads = threads;
		this.timePerThread = timePerThread;
		this.toBeTested = toBeTested;
		serviceExecutor = IServiceExecutor.Utils.executor(threads);
	}

	public void testMe() {
		try {
			final CountDownLatch latch = new CountDownLatch(threads);
			for (int i = 0; i < threads; i++) {
				final int thread = i;
				serviceExecutor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						Stats stats = new Stats();
						Context context = toBeTested.start(thread);
						try {
							for (int j = 0; j < timePerThread; j++) {
								long start = System.nanoTime();
								toBeTested.execute(context, thread, j);
								long duration = System.nanoTime() - start;
								stats.add(duration);
								System.out.println(String.format("Thread: %3d Index: %3d Duration: %5.2f", thread, j, duration / 1000000.0));
							}
						} finally {
							toBeTested.finished(context, thread);
							times.put(thread, stats);
							latch.countDown();
						}
						return null;
					}
				});
			}
			latch.await();
			serviceExecutor.shutdownAndAwaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw WrappedException.wrap(e);
		}
	}

	public void dumpStats() {
		for (int i = 0; i < threads; i++) {
			System.out.println(times.get(i).display("%10.2f", 1000000));
		}
	}
}