package org.arc4eclipse.utilities.events;

import java.util.ArrayList;
import java.util.List;

import org.arc4eclipse.utilities.callbacks.ICallback;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.exceptions.WrappedException;

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
