package org.softwareFm.card.internal;

import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.IMutableCardDataStore;
import org.softwareFm.card.internal.details.IAfterEditCallback;
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
	public void put(String url, Map<String, Object> map, final IAfterEditCallback afterEdit) {
		this.facard.post(url, map, new IResponseCallback() {
			@Override
			public void process(IResponse response) {
				afterEdit.afterEdit();
			}
		});
	}
}