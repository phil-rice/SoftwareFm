/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.ICardSelectedListener;
import org.softwareFm.swt.card.IHasCardConfig;
import org.softwareFm.swt.card.ILineSelectedListener;
import org.softwareFm.swt.card.dataStore.CardAndCollectionDataStoreVisitorMonitored;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.menu.IPopupMenuService;

public class HoldsCardHolder extends Composite implements IHasCardConfig {

	protected final List<ICardSelectedListener> listeners = new CopyOnWriteArrayList<ICardSelectedListener>();
	protected final CardConfig cardConfig;
	private final IContainer container;

	public HoldsCardHolder(Composite parent, int style, IContainer container, CardConfig cardConfig) {
		super(parent, style);
		this.container = container;
		this.cardConfig = cardConfig.withStyleAndSelection(cardConfig.cardStyle, false).withPopupMenuService(IPopupMenuService.Utils.<ICard> noPopupMenus());
	}

	public void addCardSelectedListener(ICardSelectedListener listener) {
		listeners.add(listener);
	}

	public void makeCardHolder(String url, String title) {
		ICardHolder cardHolder = ICardHolder.Utils.cardHolderWithLayout(this, cardConfig,  Arrays.asList(url), null);
		cardHolder.getComposite().setLayout(new CardHolder.CardHolderLayout());
		addPaintListenerThatGetsMoreData(cardHolder, url);
	}

	private void addPaintListenerThatGetsMoreData(final ICardHolder cardHolder, final String url) {
		PaintListener listener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				cardHolder.getControl().removePaintListener(this);
				if (!cardHolder.getControl().isDisposed()) {
					// System.out.println("PaintListener... get data for: " + url);
					CardAndCollectionDataStoreVisitorMonitored visitor = new CardAndCollectionDataStoreVisitorMonitored() {
						@Override
						public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, final ICard card) {
							// System.out.println("  Initial card " + card.getControl().isDisposed());
							super.initialCard(cardHolder, cardConfig, url, card);
							card.addLineSelectedListener(new ILineSelectedListener() {
								@Override
								public void selected(ICard card, String key, Object value) {
									notifyCardSelectedListeners(card);
								}
							});
							cardHolder.addCardTitleSelectedListener(new ICardSelectedListener() {
								@Override
								public void cardSelected(String cardUrl) {
									notifyCardSelectedListeners(card);
								}
							});
						}
					};

					cardConfig.cardCollectionsDataStore.processDataFor(container, cardHolder, cardConfig, url, visitor);
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