/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.aggregators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class CrossThreadAggregator<T> implements Iterable<T> {

	private final List<T>[] results;
	private final AtomicInteger index = new AtomicInteger(0);
	private final ThreadLocal<List<T>> lists = new ThreadLocal<List<T>>() {

		@Override
		protected java.util.List<T> initialValue() {
			ArrayList<T> list = new ArrayList<T>();
			results[index.getAndIncrement()] = list;
			return list;
		}
	};

	@SuppressWarnings("unchecked")
	public CrossThreadAggregator(int maxSize) {
		this.results = new List[maxSize];
	}

	public List<T> myList() {
		return lists.get();
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private Iterator<T> iterator = index.get() == 0 ? null : results[0].iterator();
			private int listIndex = 1;

			@Override
			public boolean hasNext() {
				if (iterator == null)
					return false;
				if (iterator.hasNext())
					return true;
				int max = index.get();
				while (listIndex < max && !iterator.hasNext()) {
					List<T> result = results[listIndex++];
					if (result == null) {// this is avoiding a race condition in add between index.incrementAndGet and results[i]=list
						iterator = null;
						return false;
					}
					iterator = result.iterator();
				}
				if (iterator.hasNext())
					return true;
				iterator = null;
				return false;
			}

			@Override
			public T next() {
				if (iterator == null)
					throw new NoSuchElementException();
				return iterator.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}