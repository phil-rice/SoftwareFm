/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.events;

import java.util.ArrayList;
import java.util.List;

import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;

public class ListenerList<T> {

	private final static ICallback<Exception> defaultExceptionCallback = new ICallback<Exception>() {
		@Override
		public void process(Exception t) throws Exception {
			throw WrappedException.wrap(t);
		}
	};

	private static ListenerList<IListenerListListener> listeners = new ListenerList<IListenerListListener>(defaultExceptionCallback, false);

	public static void addListenerListListener(IListenerListListener listener) {
		listeners.add(listener);
	}

	public static void removeListenerListListener(IListenerListListener listener) {
		listeners.remove(listener);
	}

	public static <T> void fireListenerListListeners(final List<?> actualListeners, final ICallback<T> callback) {
		ICallback<IListenerListListener> listenerCallback = new ICallback<IListenerListListener>() {
			@Override
			public void process(IListenerListListener t) throws Exception {
				t.eventOccured(actualListeners, callback);
			}
		};
		listeners.fire(listenerCallback);
	}

	private final List<T> list = Lists.newList();
	private final Object lock = new Object();
	private final ICallback<Exception> exceptionHandler;
	private boolean report;

	public ListenerList() {
		this(defaultExceptionCallback, true);
	}

	public ListenerList(ICallback<Exception> callback) {
		this(callback, true);
	}

	private ListenerList(ICallback<Exception> exceptionHandler, boolean report) {
		this.exceptionHandler = exceptionHandler;
		this.report = report;
	}

	public void add(T listener) {
		synchronized (lock) {
			list.add(listener);
		}

	}

	public void remove(T listener) {
		synchronized (lock) {
			list.remove(listener);
		}
	}

	public void fire(ICallback<T> callback) {
		if (report)
			fireListenerListListeners(getCopyOfList(), callback);
		for (T listener : getCopyOfList())
			try {
				callback.process(listener);
			} catch (Exception e) {
				try {
					this.exceptionHandler.process(e);
				} catch (Exception e1) {
					throw WrappedException.wrap(e1);
				}
			}
	}

	private List<T> getCopyOfList() {
		synchronized (lock) {
			return new ArrayList<T>(list);
		}

	}
}