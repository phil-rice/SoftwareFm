package org.softwareFm.card.card.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.card.ICardSelectedListener;
import org.softwareFm.card.card.IHasCardConfig;
import org.softwareFm.card.card.ILineSelectedListener;
import org.softwareFm.card.card.IPopupMenuContributor;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreVisitorMonitored;

public class HoldsCardHolder extends Composite implements IHasCardConfig {

	protected final List<ICardSelectedListener> listeners = new CopyOnWriteArrayList<ICardSelectedListener>();
	protected final CardConfig cardConfig;

	public HoldsCardHolder(Composite parent, int style, CardConfig cardConfig) {
		super(parent, style);
		this.cardConfig = cardConfig.withStyleAndSelection(cardConfig.cardStyle, false).withPopupMenuContributor(IPopupMenuContributor.Utils.<ICard>noContributor());
	}

	public void addCardSelectedListener(ICardSelectedListener listener) {
		listeners.add(listener);
	}

	public void makeCardHolder(String url, String title) {
		ICardHolder cardHolder = ICardHolder.Utils.cardHolderWithLayout(this, "loading", title, cardConfig, url, null);
		cardHolder.getComposite().setLayout(new CardHolder.CardHolderLayout());
		addPaintListenerThatGetsMoreData(cardHolder, url);
	}

	private void addPaintListenerThatGetsMoreData(final ICardHolder cardHolder, final String url) {
		PaintListener listener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				cardHolder.getControl().removePaintListener(this);
				if (!cardHolder.getControl().isDisposed()) {
					CardAndCollectionDataStoreVisitorMonitored visitor = new CardAndCollectionDataStoreVisitorMonitored() {
						@Override
						public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, final ICard card) {
							super.initialCard(cardHolder, cardConfig, url, card);
							card.addLineSelectedListener(new ILineSelectedListener() {
								@Override
								public void selected(ICard card, String key, Object value) {
									notifyCardSelectedListeners(card);
								}
							});
						}

					};

					cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, visitor);
				}
			}
		};
		cardHolder.getControl().addPaintListener(listener);
	}

	private void notifyCardSelectedListeners(final ICard card) {
		for (ICardSelectedListener listener : listeners) {
			listener.cardSelected(card.url());
		}
	}

	@Override
	public CardConfig getCardConfig() {
		return cardConfig;
	}

}
