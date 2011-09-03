package org.softwareFm.utilities.aggregators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class CrossThreadAggregator<T> implements Iterable<T> {

	private final List<T>[] results;
	private final AtomicInteger index = new AtomicInteger(0);
	private final ThreadLocal<List<T>> lists = new ThreadLocal<List<T>>() {
		
		protected java.util.List<T> initialValue() {
			ArrayList<T> list = new ArrayList<T>();
			results[index.getAndIncrement()] = list;
			return list;
		};
	};

	@SuppressWarnings("unchecked")
	public CrossThreadAggregator(int maxSize) {
		this.results = new List[maxSize];
	}

	public List<T> myList() {
		return lists.get();
	}

	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private Iterator<T> iterator = index.get() == 0 ? null : results[0].iterator();
			private int listIndex = 1;

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

			public T next() {
				if (iterator == null)
					throw new NoSuchElementException();
				return iterator.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
