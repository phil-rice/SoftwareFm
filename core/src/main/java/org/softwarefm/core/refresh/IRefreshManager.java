package org.softwarefm.core.refresh;

import org.softwarefm.core.refresh.internal.RefreshManager;
import org.softwarefm.utilities.events.IMultipleListenerList;

public interface IRefreshManager extends IRefresh {

	void addRefreshListener(IRefresh listener);

	void removeRefreshListener(IRefresh listener);

	public static class Utils {
		public static IRefreshManager refreshManager(IMultipleListenerList multipleListenerList) {
			return new RefreshManager(multipleListenerList);
		}
	}
}
