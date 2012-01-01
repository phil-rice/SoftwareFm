/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.history;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.softwareFm.utilities.collections.Lists;

public class History<T> implements IHistory<T> {

	private final List<T> history = Lists.newList();
	private int index;
	protected final Object lock = new Object();
	private final List<IHistoryListener<T>> listeners = new CopyOnWriteArrayList<IHistoryListener<T>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softwareFm.card.internal.IHistory#push(T)
	 */
	@Override
	public void push(T newItem) {
		synchronized (lock) {
			if (history.size() > 0 && newItem.equals(history.get(index)))
				return;
			Lists.removeAllAfter(history, index);
			index = history.size();
			history.add(newItem);
			fireListeners(newItem);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softwareFm.card.internal.IHistory#addHistoryListener(org.softwareFm.card.internal.IHistoryListener)
	 */
	@Override
	public void addHistoryListener(IHistoryListener<T> listener) {
		listeners.add(listener);
	}

	private void fireListeners(T newValue) {
		for (IHistoryListener<T> listener : listeners)
			listener.changingTo(newValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softwareFm.card.internal.IHistory#prev()
	 */
	@Override
	public T previous() {
		synchronized (lock) {
			if (index > 0)
				index--;
			T result = history.get(index);
			fireListeners(result);
			return result;
		}
	}

	@Override
	public T last() {
		synchronized (lock) {
			return history.get(index);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softwareFm.card.internal.IHistory#next()
	 */
	@Override
	public T next() {
		synchronized (lock) {
			if (index + 1 < history.size())
				index++;
			T result = history.get(index);
			fireListeners(result);
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softwareFm.card.internal.IHistory#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return hasNextInHistory();
	}

	protected boolean hasNextInHistory() {
		synchronized (lock) {
			return index < history.size() - 1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softwareFm.card.internal.IHistory#hasPrev()
	 */
	@Override
	public boolean hasPrevious() {
		synchronized (lock) {
			return index > 0;
		}
	}

	public List<T> items() {
		synchronized (lock) {
			return new ArrayList<T>(history);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.softwareFm.card.internal.IHistory#getItem(int)
	 */
	@Override
	public T getItem(int i) {
		synchronized (lock) {
			return history.get(i);
		}
	}

	@Override
	public int size() {
		synchronized (lock) {
			return history.size();
		}
	}

	@Override
	public Iterator<T> iterator() {
		return history.iterator();
	}

}