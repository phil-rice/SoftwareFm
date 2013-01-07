package org.softwarefm.utilities.events;

import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.internal.ListenerList;

public interface IListenerList<L> {

	void addListener(L listener);

	void removeListener(L listener);

	void fire(ICallback<L> callback);

	void clear();

	boolean contains(L listener);

	int size();

	public static class Utils {
		public static <L> IListenerList<L> list(IMultipleListenerList list, Class<L> clazz, Object source) {
			return new ListenerList<L>(list, clazz, source);
		}

		public static <L> IListenerList<L> empty() {
			return new IListenerList<L>() {
				@Override
				public void addListener(L listener) {
				}

				@Override
				public void removeListener(L listener) {
				}

				@Override
				public void fire(ICallback<L> callback) {
				}

				@Override
				public void clear() {
				}

				@Override
				public boolean contains(L listener) {
					return false;
				}

				@Override
				public int size() {
					return 0;
				}
			};
		}
	}

}
