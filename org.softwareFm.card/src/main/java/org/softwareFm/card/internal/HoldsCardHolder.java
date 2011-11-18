package org.softwareFm.card.internal;

import java.util.List;
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
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.IPopupMenuContributor;

public class HoldsCardHolder extends Composite {

	protected final List<ICardSelectedListener> listeners = new CopyOnWriteArrayList<ICardSelectedListener>();
	protected final CardConfig cardConfig;
	private CardHolder cardHolder;

	public HoldsCardHolder(Composite parent, int style, CardConfig cardConfig) {
		super(parent, style);
		this.cardConfig = cardConfig.withStyleAndSelection(cardConfig.cardStyle, false).withPopupMenuContributor(IPopupMenuContributor.Utils.noContributor());
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
						public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, final ICard card) {
							super.initialCard(cardHolder, cardConfig, url, card);
							card.getControl().addMouseListener(new MouseAdapter() {
								@Override
								public void mouseUp(MouseEvent e) {
									norifyCardSelectedListeners(card);
								}
							});
							card.addLineSelectedListener(new ILineSelectedListener() {
								
								@Override
								public void selected(ICard card, String key, Object value) {
									norifyCardSelectedListeners(card);
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

	private void norifyCardSelectedListeners(final ICard card) {
		for (ICardSelectedListener listener : listeners) {
			listener.cardSelected(card);
		}
	}

	public CardHolder getCardHolder() {
		return cardHolder;
	}
}
