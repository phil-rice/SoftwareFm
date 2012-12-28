package org.softwarefm.utilities.events;

import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.internal.ListenerList;

public interface IListenerList<L> {

	void addListener(L listener);

	void fire(ICallback<L> callback);

	public static class Utils {
		public static <L> IListenerList<L> list(IMultipleListenerList list, Object source) {
			return new ListenerList<L>(list, source);
		}
	}

}