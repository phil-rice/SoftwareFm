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
import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.dataStore.IAfterEditCallback;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacardConstants.RepositoryFacardConstants;
import org.softwareFm.utilities.exceptions.WrappedException;

public class CardDataStoreForRepository implements IMutableCardDataStore {
	private final Control from;
	private final IRepositoryFacard facard;

	public CardDataStoreForRepository(Control from, IRepositoryFacard facard) {
		this.from = from;
		this.facard = facard;
	}

	@Override
	public void clearCaches() {
		facard.clearCaches();

	}

	@Override
	public Future<?> makeRepo(final String url, final IAfterEditCallback callback) {
		return facard.makeRoot(url, new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				callback.afterEdit(url);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Future<T> processDataFor(final String url, final ICardDataStoreCallback<T> callback) {
		@SuppressWarnings("rawtypes")
		Future future = facard.get(url, new IRepositoryFacardCallback() {
			@Override
			public void process(final IResponse response, final Map<String, Object> data) {
				if (!from.isDisposed())
					from.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								if (RepositoryFacardConstants.okStatusCodes.contains(response.statusCode()))
									callback.process(url, data);
								else {
									if (IRepositoryFacard.Utils.debug)
										System.out.println(response.asString());
									callback.noData(url);
								}
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
						}
					});
			}
		});
		return future;
	}

	@Override
	public Future<?> put(final String url, Map<String, Object> map, final IAfterEditCallback afterEdit) {
		return this.facard.post(url, map, new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				if (!from.isDisposed())
					if (!from.isDisposed())
						from.getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								afterEdit.afterEdit(url);
							}
						});
			}
		});
	}

	@Override
	public void clearCache(String url) {
	}
}