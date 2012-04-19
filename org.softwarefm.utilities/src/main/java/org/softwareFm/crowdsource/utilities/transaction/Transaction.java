/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.transaction;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;

public class Transaction<T> implements ITransaction<T> {

	private final long defaultTimeout;
	private boolean cancelled;
	private final Future<T> future;

	public Transaction(Future<T> future, long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
		this.future = future;
	}

	@Override
	public T get(long millisecondsToWait) {
		try {
			return future.get(defaultTimeout, TimeUnit.MILLISECONDS);
		} catch (ExecutionException e) {
			throw WrappedException.wrap(e.getCause());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public T get() {
		return get(defaultTimeout);
	}

	@Override
	public void cancel() {
		cancelled = true;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

}