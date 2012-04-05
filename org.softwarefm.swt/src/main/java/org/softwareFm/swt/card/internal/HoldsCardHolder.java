/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.ICardSelectedListener;
import org.softwareFm.swt.card.IHasCardConfig;
import org.softwareFm.swt.card.ILineSelectedListener;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.CardDataStoreCallbackAdapter;
import org.softwareFm.swt.menu.IPopupMenuService;

public class HoldsCardHolder extends Composite implements IHasCardConfig {

	protected final List<ICardSelectedListener> listeners = new CopyOnWriteArrayList<ICardSelectedListener>();
	protected final CardConfig cardConfig;

	public HoldsCardHolder(Composite parent, int style, CardConfig cardConfig) {
		super(parent, style);
		this.cardConfig = cardConfig.withStyleAndSelection(cardConfig.cardStyle, false).withPopupMenuService(IPopupMenuService.Utils.<ICard> noPopupMenus());
	}

	public void addCardSelectedListener(ICardSelectedListener listener) {
		listeners.add(listener);
	}

	public void makeCardHolder(String url, String title) {
		ICardHolder cardHolder = ICardHolder.Utils.cardHolderWithLayout(this, cardConfig, Arrays.asList(url), null);
		cardHolder.getComposite().setLayout(new CardHolder.CardHolderLayout());
		addPaintListenerThatGetsMoreData(cardHolder, url);
	}

	private void addPaintListenerThatGetsMoreData(final ICardHolder cardHolder, final String url) {
		PaintListener listener = new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				cardHolder.getControl().removePaintListener(this);
				if (!cardHolder.getControl().isDisposed()) {
					cardConfig.cardDataStore.processDataFor(url, new CardDataStoreCallbackAdapter<Void>() {
						@Override
						public Void process(String url, Map<String, Object> result) throws Exception {
							final ICard card = cardConfig.cardFactory.makeCard(cardHolder, cardConfig, url, result);
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
							return null;
						}
					});
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