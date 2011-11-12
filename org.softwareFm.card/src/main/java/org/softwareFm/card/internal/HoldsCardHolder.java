package org.softwareFm.card.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardAndCollectionDataStoreVisitorMonitored;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.card.api.ICardSelectedListener;

public class HoldsCardHolder extends Composite {

	protected final List<ICardSelectedListener> listeners = new CopyOnWriteArrayList<ICardSelectedListener>();
	protected final CardConfig cardConfig;
	private CardHolder cardHolder;

	public HoldsCardHolder(Composite parent, int style, CardConfig cardConfig) {
		super(parent, style);
		this.cardConfig = cardConfig;
	}

	public void addCardSelectedListener(ICardSelectedListener listener) {
		listeners.add(listener);
	}

	public void makeCardHolder(String url, String title) {
		cardHolder = new CardHolder(this, "loading", title, cardConfig, url, null);
		cardHolder.getComposite().setLayout(new CardHolder.CardHolderLayout());
		addPaintListenerThatGetsMoreData(url, cardHolder);
	}

	private void addPaintListenerThatGetsMoreData(final String url, final CardHolder cardHolder) {
		PaintListener listener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				cardHolder.getControl().removePaintListener(this);
				if (!cardHolder.getControl().isDisposed()) {
					CardAndCollectionDataStoreVisitorMonitored visitor = new CardAndCollectionDataStoreVisitorMonitored() {
						@Override
						public void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url) {
						}

						@Override
						public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, final ICard card) {
							card.getControl().addMouseListener(new MouseAdapter() {
								@Override
								public void mouseUp(MouseEvent e) {
									for (ICardSelectedListener listener : listeners) {
										listener.cardSelected(card);
									}
								}
							});
						}

						@Override
						public void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment) {
						}

						@Override
						public void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result) {
						}

						@Override
						public void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl) {
						}

						@Override
						public void finished(ICardHolder cardHolder, String url, ICard card) {
						}
					};

					cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, visitor);
				}
			}
		};
		cardHolder.getControl().addPaintListener(listener);
	}

	public CardHolder getCardHolder() {
		return cardHolder;
	}
}
