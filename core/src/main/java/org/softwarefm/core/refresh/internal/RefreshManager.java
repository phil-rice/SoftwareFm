package org.softwarefm.core.refresh.internal;

import org.softwarefm.core.refresh.IRefresh;
import org.softwarefm.core.refresh.IRefreshManager;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IListenerList;
import org.softwarefm.utilities.events.IMultipleListenerList;

public class RefreshManager implements IRefreshManager {

	private final IListenerList<IRefresh> listenerList;
	private static final ICallback<IRefresh> callback = new ICallback<IRefresh>() {
		@Override
		public void process(IRefresh t) throws Exception {
			t.refresh();
		}
	};

	public RefreshManager(IMultipleListenerList multipleListenerList) {
		this.listenerList = IListenerList.Utils.list(multipleListenerList, IRefresh.class, this);
	}

	@Override
	public void refresh() {
		listenerList.fire(callback);
	}

	@Override
	public void addRefreshListener(IRefresh listener) {
		listenerList.addListener(listener);

	}

	@Override
	public void removeRefreshListener(IRefresh listener) {
		listenerList.removeListener(listener);
	}

}
