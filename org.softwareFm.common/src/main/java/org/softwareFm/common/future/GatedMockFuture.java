/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.functions.IFunction1;

public class GatedMockFuture<From, To> implements Future<To> {

	private final From from;
	private boolean done;
	private final IFunction1<From, To> function;
	private To result;

	public GatedMockFuture(IFunction1<From, To> function, From from) {
		super();
		this.function = function;
		this.from = from;
	}

	public void kick() {
		try {
			result = function.apply(from);
			done = true;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public boolean cancel(boolean arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public To get() throws InterruptedException, ExecutionException {
		if (!done)
			throw new IllegalStateException();
		return result;
	}

	@Override
	public To get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException {
		if (!done)
			throw new IllegalStateException();
		return result;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return done;
	}

}