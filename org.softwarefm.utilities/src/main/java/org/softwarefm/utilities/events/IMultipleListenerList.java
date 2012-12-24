package org.softwarefm.utilities.events;

import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.internal.MultipleListenerList;

public interface IMultipleListenerList {

	<L> void addListener(Object source, L listener);

	<L> void fire(Object source, ICallback<L> callback);

	public static class Utils {
		public static  IMultipleListenerList defaultList() {
			return new MultipleListenerList();
		}
	}

}
