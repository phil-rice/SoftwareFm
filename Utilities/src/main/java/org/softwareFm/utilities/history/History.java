package org.softwareFm.utilities.history;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.softwareFm.utilities.collections.Lists;

public class History<T> implements IHistory<T> {

	private final List<T> history = Lists.newList();
	private int index;
	private final Object lock = new Object();
	private final List<IHistoryListener<T>> listeners = new CopyOnWriteArrayList<IHistoryListener<T>>();

	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see org.softwareFm.card.internal.IHistory#addHistoryListener(org.softwareFm.card.internal.IHistoryListener)
	 */
	@Override
	public void addHistoryListener(IHistoryListener<T> listener) {
		listeners.add(listener);
	}
	
	private void fireListeners(T newValue){
		for (IHistoryListener<T> listener: listeners)
			listener.changingTo(newValue);
	}

	/* (non-Javadoc)
	 * @see org.softwareFm.card.internal.IHistory#prev()
	 */
	@Override
	public T prev() {
		synchronized (lock) {
			if (index > 0)
				index--;
			T result = history.get(index);
			fireListeners(result);
			return result;
		}
	}

	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see org.softwareFm.card.internal.IHistory#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return index < history.size() - 1;
	}

	/* (non-Javadoc)
	 * @see org.softwareFm.card.internal.IHistory#hasPrev()
	 */
	@Override
	public boolean hasPrev() {
		return index > 0;
	}

	public List<T> items() {
		return history;
	}

	/* (non-Javadoc)
	 * @see org.softwareFm.card.internal.IHistory#getItem(int)
	 */
	@Override
	public T getItem(int i) {
		return history.get(i);
	}

}