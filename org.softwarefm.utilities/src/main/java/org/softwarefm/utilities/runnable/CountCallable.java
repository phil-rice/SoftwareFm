/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.runnable;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class CountCallable<T> implements Callable<T> {

	private final T value;
	private final AtomicInteger count = new AtomicInteger();

	public CountCallable(T value) {
		super();
		this.value = value;
	}

	@Override
	public T call() throws Exception {
		count.incrementAndGet();
		return value;
	}

	public int getCount() {
		return count.get();
	}

}