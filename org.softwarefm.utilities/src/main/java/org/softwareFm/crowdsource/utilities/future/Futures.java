/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;

public class Futures {

	public static <T> Future<T> bindToMonitor(final Future<T> rawFuture, final IMonitor rawMonitor){
		return new Future<T>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				rawMonitor.cancel();
				return rawFuture.cancel(mayInterruptIfRunning);
			}

			@Override
			public boolean isCancelled() {
				return rawFuture.isCancelled();
			}

			@Override
			public boolean isDone() {
				return rawFuture.isDone();
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				return rawFuture.get();
			}

			@Override
			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				return rawFuture.get(timeout, unit);
			}
			
			@Override
			public String toString() {
				return "BindToMonitor(" + rawFuture +", " + rawMonitor +")";
			}
		};
	}
	
	public static <T> Future<T> doneFuture(final T value) {
		return new Future<T>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public boolean isCancelled() {
				return false;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public T get() throws InterruptedException, ExecutionException {
				return value;
			}

			@Override
			public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				return value;
			}
		};
	}

	public static <From, To> Future<To> transformed(final Future<From> future, final IFunction1<From, To> transformer) {
		return new Future<To>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return future.cancel(mayInterruptIfRunning);
			}

			@Override
			public To get() throws InterruptedException, ExecutionException {
				try {
					return transformer.apply(future.get());
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}

			@Override
			public To get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				try {
					return transformer.apply(future.get(timeout, unit));
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}

			@Override
			public boolean isCancelled() {
				return future.isCancelled();
			}

			@Override
			public boolean isDone() {
				return future.isDone();
			}
		};
	}

	public static <From, To> GatedMockFuture<From, To> gatedMock(IFunction1<From, To> function, final From result) {
		return new GatedMockFuture<From, To>(function, result);
	}

	public static <T> GatedFuture<T> gatedFuture() {
		return new GatedFuture<T>();
	}

}