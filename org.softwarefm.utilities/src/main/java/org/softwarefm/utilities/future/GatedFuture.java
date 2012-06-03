/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GatedFuture<T> implements Future<T> {

	private final CountDownLatch latch = new CountDownLatch(1);
	private T value;

	public void done(T value) {
		this.value = value;
		this.latch.countDown();
	}

	
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	
	public T get() throws InterruptedException, ExecutionException {
		latch.await();
		return value;
	}

	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		latch.await(timeout, unit);
		return value;
	}

	
	public boolean isCancelled() {
		return false;
	}

	
	public boolean isDone() {
		return latch.getCount() == 0;
	}

}