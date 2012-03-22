/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.dataStore.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.crowdsource.api.ICrowdSourcedReadWriteApi;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.swt.constants.CardMessages;
import org.softwareFm.swt.dataStore.IAfterEditCallback;
import org.softwareFm.swt.dataStore.ICardDataStoreCallback;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;
import org.softwareFm.swt.swt.Swts;

public class CardDataStoreForRepository implements IMutableCardDataStore {
	private final Control control;
	private final IServiceExecutor serviceExecutor;
	private final ICrowdSourcedReadWriteApi readWriteApi;
	private final File root;

	public CardDataStoreForRepository(Control from, IServiceExecutor serviceExecutor, ICrowdSourcedReadWriteApi readWriteApi) {
		this.control = from;
		this.serviceExecutor = serviceExecutor;
		this.readWriteApi = readWriteApi;
		this.root = readWriteApi.gitOperations().getRoot();
	}

	@Override
	public void clearCache(final String url) {
		readWriteApi.modify(IGitLocal.class, new ICallback<IGitLocal>() {
			@Override
			public void process(IGitLocal gitLocal) throws Exception {
				gitLocal.clearCache(url);
			}
		});
	}

	@Override
	public void refresh(String url) {
		clearCache(url);
		Files.deleteDirectory(new File(root, url));
	}

	@Override
	public void clearCaches() {
		readWriteApi.modify(IGitLocal.class, new ICallback<IGitLocal>() {
			@Override
			public void process(IGitLocal gitLocal) throws Exception {
				gitLocal.clearCaches();
			}
		});
	}

	@Override
	public Future<?> makeRepo(final String url, final IAfterEditCallback callback) {
		return serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(IMonitor from) throws Exception {
				from.beginTask(MessageFormat.format(CardMessages.makeRepo, url), 2);
				try {
					readWriteApi.modify(IGitLocal.class, new ICallback<IGitLocal>() {
						@Override
						public void process(IGitLocal gitLocal) throws Exception {
							gitLocal.init(url);
						}
					});
					from.worked(1);
					callback.afterEdit(url);
					return null;
				} finally {
					from.done();
				}
			}
		});
	}

	@Override
	public void delete(final String url, final IAfterEditCallback callback) {
		serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(IMonitor from) throws Exception {
				from.beginTask(MessageFormat.format(CardMessages.delete, url), 2);
				try {
					readWriteApi.modify(IGitLocal.class, new ICallback<IGitLocal>() {
						@Override
						public void process(IGitLocal gitLocal) throws Exception {
							gitLocal.init(url);
						}
					});
					from.worked(1);
					callback.afterEdit(url);
					return null;
				} finally {
					from.done();
				}
			}
		});

	}

	@Override
	public <T> Future<T> processDataFor(final String url, final ICardDataStoreCallback<T> callback) {
		final IFileDescription fileDescription = IFileDescription.Utils.plain(url);
		return serviceExecutor.submit(new IFunction1<IMonitor, T>() {
			@Override
			public T apply(final IMonitor from) throws Exception {
				from.beginTask(MessageFormat.format(CardMessages.processDataFor, url), 2);
				final Map<String, Object> data = IGitReader.Utils.getFileAndDescendants(readWriteApi, fileDescription);
				from.worked(1);
				Swts.asyncExecAndMarkDone(control, from, new Runnable() {
					@Override
					public void run() {
						if (data == null || data.size() == 0)
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
		return serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(IMonitor from) throws Exception {
				from.beginTask(MessageFormat.format(CardMessages.put, url, map), 2);
				final IFileDescription fileDescription = IFileDescription.Utils.plain(url);
				readWriteApi.modify(IGitLocal.class, new ICallback<IGitLocal>() {
					@Override
					public void process(IGitLocal gitLocal) throws Exception {
						gitLocal.put(fileDescription, map);
					}
				});
				from.worked(1);
				Swts.asyncExecAndMarkDone(control, from, new Runnable() {
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