/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.dataStore.internal;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.dataStore.IAfterEditCallback;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitLocal;
import org.softwareFm.utilities.services.IServiceExecutor;

public class CardDataStoreForRepository implements IMutableCardDataStore {
	private final Control control;
	private final IGitLocal gitLocal;
	private final IServiceExecutor serviceExecutor;

	public CardDataStoreForRepository(Control from, IServiceExecutor serviceExecutor, IGitLocal facard) {
		this.control = from;
		this.serviceExecutor = serviceExecutor;
		this.gitLocal = facard;
	}

	@Override
	public void clearCache(String url) {
		gitLocal.clearCache(url);
	}

	@Override
	public void clearCaches() {
		gitLocal.clearCaches();
	}

	@Override
	public Future<?> makeRepo(final String url, final IAfterEditCallback callback) {
		return serviceExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				gitLocal.init(url);
				callback.afterEdit(url);
				return null;
			}
		});
	}

	@Override
	public void delete(final String url, final IAfterEditCallback callback) {
		serviceExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				gitLocal.delete(IFileDescription.Utils.plain(url));
				callback.afterEdit(url);
				return null;
			}
		});

	}

	@Override
	public <T> Future<T> processDataFor(final String url, final ICardDataStoreCallback<T> callback) {
		final IFileDescription fileDescription = IFileDescription.Utils.plain(url);
		return serviceExecutor.submit(new Callable<T>() {
			@Override
			public T call() throws Exception {
				final Map<String, Object> data = gitLocal.getFile(fileDescription);
				Swts.asyncExec(control, new Runnable() {
					@Override
					public void run() {
						if (data.size() == 0)
							ICardDataStoreCallback.Utils.noData(callback, url);
						else
							ICardDataStoreCallback.Utils.process(callback, url, data);
					}
				});
				return null;
			}
		});
	}

	@Override
	public Future<?> put(final String url, final Map<String, Object> map, final IAfterEditCallback afterEdit) {
		return serviceExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				IFileDescription fileDescription = IFileDescription.Utils.plain(url);
				gitLocal.put(fileDescription, map);
				Swts.asyncExec(control, new Runnable() {
					@Override
					public void run() {
						afterEdit.afterEdit(url);

					}
				});
				return null;
			}
		});
	}

}