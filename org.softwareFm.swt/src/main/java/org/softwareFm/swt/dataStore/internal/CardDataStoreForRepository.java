/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.dataStore.internal;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.ISwtFunction1;
import org.softwareFm.swt.dataStore.IAfterEditCallback;
import org.softwareFm.swt.dataStore.ICardDataStore;
import org.softwareFm.swt.dataStore.ICardDataStoreCallback;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;

public class CardDataStoreForRepository implements IMutableCardDataStore {

	public static Logger logger = Logger.getLogger(ICardDataStore.class);
	private final IContainer readWriteApi;
	private final File root;

	public CardDataStoreForRepository(IContainer readWriteApi) {
		this.readWriteApi = readWriteApi;
		this.root = readWriteApi.gitOperations().getRoot();
	}

	@Override
	public void clearCache(final String url) {
		logger.debug("clearCache(" + url + ")");
		readWriteApi.access(IGitLocal.class, new ICallback<IGitLocal>() {
			@Override
			public void process(IGitLocal gitLocal) throws Exception {
				gitLocal.clearCache(url);
			}

			@Override
			public String toString() {
				return CardDataStoreForRepository.this.getClass().getSimpleName() + ".clearCache(" + url + ")";
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
		logger.debug("clearCaches");
		readWriteApi.access(IGitLocal.class, new ICallback<IGitLocal>() {
			@Override
			public void process(IGitLocal gitLocal) throws Exception {
				gitLocal.clearCaches();
			}

			@Override
			public String toString() {
				return CardDataStoreForRepository.this.getClass().getSimpleName() + ".clearCaches";
			}
		}).get();
	}

	@Override
	public ITransaction<?> makeRepo(final String url, final IAfterEditCallback callback) {
		logger.debug("makeRepo: " + url);
		return readWriteApi.accessWithCallback(IGitLocal.class, new IFunction1<IGitLocal, Void>() {
			@Override
			public Void apply(IGitLocal gitLocal) throws Exception {
				gitLocal.init(url, "CardDataStore.makeRepo(" + url + ")");
				return null;
			}

			@Override
			public String toString() {
				return CardDataStoreForRepository.this.getClass().getSimpleName() + ".makeRepo(" + url + ")";
			}

		}, new ICallback<Void>() {
			@Override
			public void process(Void t) throws Exception {
				callback.afterEdit(url);
			}

			@Override
			public String toString() {
				return "afterEdit";
			}
		});
	}

	@Override
	@SuppressWarnings("delete not implemented or tested")
	public void delete(final String url, final IAfterEditCallback callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ITransaction<T> processDataFor(final String url, final ICardDataStoreCallback<T> callback) {
		logger.debug("processDataFor: " + url);
		final IFileDescription fileDescription = IFileDescription.Utils.plain(url);
		return readWriteApi.accessWithCallbackFn(IGitLocal.class, new IFunction1<IGitLocal, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(IGitLocal from) throws Exception {
				logger.debug("  processDataFor/getFileAndDescendants: " + url);
				final Map<String, Object> data = IGitReader.Utils.getFileAndDescendants1(readWriteApi, fileDescription);
				logger.debug("  processDataFor/gotFileAndDescendants: " + url + ", " + data);
				return data;
			}

			@Override
			public String toString() {
				return CardDataStoreForRepository.this.getClass().getSimpleName() + ".processDataFor(" + url + ", " + callback + ")";
			}
		}, new ISwtFunction1<Map<String, Object>, T>() {
			@Override
			public T apply(Map<String, Object> data) throws Exception {
				logger.debug("  processDataFor/swtFunc: " + url + ", " + data);
				if (data == null || data.size() == 0)
					return ICardDataStoreCallback.Utils.noData(callback, url);
				else
					return ICardDataStoreCallback.Utils.process(callback, url, data);
			}

			@Override
			public String toString() {
				return "callback";
			}

		});
	}

	@Override
	public ITransaction<?> put(final String url, final Map<String, Object> map, final IAfterEditCallback afterEdit) {
		final IFileDescription fileDescription = IFileDescription.Utils.plain(url);
		return readWriteApi.accessWithCallbackFn(IGitLocal.class, new IFunction1<IGitLocal, Void>() {
			@Override
			public Void apply(IGitLocal gitLocal) throws Exception {
				gitLocal.put(fileDescription, map, "CardDataStore.put(" + url + ", " + map + ")");
				return null;
			}

			@Override
			public String toString() {
				return CardDataStoreForRepository.this.getClass().getSimpleName() + ".put(" + url + ", " + map + ", " + afterEdit + ")";
			}

		}, new ISwtFunction1<Void, Void>() {
			@Override
			public Void apply(Void from) throws Exception {
				afterEdit.afterEdit(url);
				return null;
			}
			@Override
			public String toString() {
				return "afterEdit";
			}
		});
	}

}