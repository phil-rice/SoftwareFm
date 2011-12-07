/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardValueChangedListener;
import org.softwareFm.card.card.ILineSelectedListener;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasTable;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class Card implements ICard, IHasTable {
	public static class CardLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			CardComposite card = (CardComposite) composite;
			return card.table.getControl().computeSize(wHint, hHint);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			// System.out.println(Swts.boundsUpToShell(composite));

			CardComposite card = (CardComposite) composite;
			Rectangle ca = card.getClientArea();
			Control tableControl = card.table.getControl();
			tableControl.setBounds(ca);
			Point size = tableControl.getSize();
			if (size.x == 0)
				return;
			TableColumn nameColumn = card.table.getNameColumn();
			TableColumn valueColumn = card.table.getValueColumn();

			nameColumn.pack();
			int idealNameWidth = nameColumn.getWidth();
			int newNameWidth = (size.x * card.cardConfig.cardNameWeight) / (card.cardConfig.cardNameWeight + card.cardConfig.cardValueWeight);
			int maxNameValue = (int) (idealNameWidth * card.cardConfig.cardMaxNameSizeRatio);
			if (newNameWidth > maxNameValue)
				newNameWidth = maxNameValue;
			int newValueWidth = size.x - newNameWidth - 1;
			nameColumn.setWidth(newNameWidth);
			valueColumn.setWidth(newValueWidth);
		}

	}

	public static class CardComposite extends Composite {
		/** Holds strategies and values to control how the card is displayed */
		final CardConfig cardConfig;
		/** The original data passed to you. Kept for debugging / tests */
		private final Map<String, Object> rawData;
		/** the current view of the data. It may change as more information is acquired from the server. It will often have been aggregated */
		private final Map<String, Object> data;

		/** The url that this card is a representation of */
		private final String url;
		/** The gui component that displays the data */
		final CardTable table;

		/** Controls access to data */
		private final Object lock = new Object();
		public String cardType;

		public CardComposite(Composite parent, final CardConfig cardConfig, final String url, Map<String, Object> rawData, final String cardType, TitleSpec titleSpec) {
			super(parent, SWT.NULL);
			if (url == null)
				throw new NullPointerException();

			this.cardConfig = cardConfig;
			this.url = url;
			this.rawData = rawData;
			this.cardType = cardType;
			Map<String, Object> modified = cardConfig.modify(url, rawData);
			data = modified == rawData ? Maps.copyMap(rawData) : modified;

			this.table = new CardTable(this, cardConfig, titleSpec, cardType, data);

			CardOutlinePaintListener cardOutlinePaintListener = new CardOutlinePaintListener(titleSpec, cardConfig);
			addPaintListener(cardOutlinePaintListener);
		}

		@Override
		public Rectangle getClientArea() {
			// note that the topMargin doesn't reference this component: it affects the space between the top of somewhere and the title.
			// There is a two pixel gap between the top of the card and the title
			Rectangle clientArea = super.getClientArea();
			int cardWidth = clientArea.width - cardConfig.rightMargin - cardConfig.leftMargin;
			int cardHeight = clientArea.height - cardConfig.bottomMargin - 2;
			Rectangle result = new Rectangle(clientArea.x + cardConfig.leftMargin, clientArea.y + 2, cardWidth, cardHeight);
			return result;
		}

		private void valueChanged(String key, Object newValue) {
			synchronized (lock) {
				table.setNewValue(key, newValue);
				data.put(key, newValue);
			}
		}

		public Map<String, Object> data() {
			synchronized (lock) {
				return Maps.copyMap(data);
			}
		}

	}

	private final CardComposite content;
	private final List<ICardValueChangedListener> valueChangedListeners = new CopyOnWriteArrayList<ICardValueChangedListener>();
	/** If a line is selected by the user, then these listeners are informed */
	private final List<ILineSelectedListener> lineSelectedListeners = new CopyOnWriteArrayList<ILineSelectedListener>();

	/** What type of card is this? Examples are the strings 'collection', 'group', 'artifact' */
	private final CardConfig cardConfig;
	private String cardType;

	public Card(Composite parent, final CardConfig cardConfig, final String url, Map<String, Object> rawData) {
		this.cardConfig = cardConfig;
		this.cardType = (String) rawData.get(CardConstants.slingResourceType);
		final TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, this);
		content = new CardComposite(parent, cardConfig, url, rawData, cardType, titleSpec);
		content.table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = content.table.getSelectionIndex();
				if (index == -1)
					return;
				String key = (String) content.table.getItem(index).getData();
				notifyLineSelectedListeners(key);
				if (!cardConfig.allowSelection)
					content.table.deselectAll();
			}

		});
		addMenuDetectListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Menu menu = new Menu(getControl());
				cardConfig.popupMenuService.contributeTo(cardConfig.popupMenuId, event, menu, Card.this);
				menu.setVisible(true);
				menu.addMenuListener(new MenuAdapter() {
					@Override
					public void menuHidden(MenuEvent e) {
						Swts.asyncExec(Card.this, new Runnable() {
							@Override
							public void run() {
								menu.dispose();
							}
						});
					}
				});
			}
		});
	}

	public void notifyLineSelectedListeners(String key) {
		for (ILineSelectedListener lineSelectedListener : lineSelectedListeners) {
			lineSelectedListener.selected(Card.this, key, content.data.get(key));
		}
	}

	@Override
	public void valueChanged(String key, Object newValue) {
		content.valueChanged(key, newValue);
		for (ICardValueChangedListener listener : valueChangedListeners)
			listener.valueChanged(this, key, newValue);

	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public void addLineSelectedListener(final ILineSelectedListener listener) {
		lineSelectedListeners.add(listener);
	}

	@Override
	public void addValueChangedListener(ICardValueChangedListener listener) {
		valueChangedListeners.add(listener);
	}

	@Override
	public CardConfig getCardConfig() {
		return cardConfig;
	}

	@Override
	public String url() {
		return content.url;
	}

	@Override
	public Map<String, Object> data() {
		return content.data();
	}

	public Map<String, Object> rawData() {
		return content.rawData;
	}

	@Override
	public String cardType() {
		return cardType;
	}

	@Override
	public String toString() {
		return "Card [url=" + url() + ", cardType=" + cardType() + ", data=" + data() + "]";
	}

	public static void main(String[] args) {
		final ICardDataStore cardDataStore = CardDataStoreFixture.rawCardStore();
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactory();

		Swts.Show.display(Card.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardConfig cardConfig = new CardConfig(cardFactory, cardDataStore).withTitleSpecFn(new IFunction1<ICardData, TitleSpec>() {
					@Override
					public TitleSpec apply(ICardData card) throws Exception {
						return TitleSpec.noTitleSpec(from.getDisplay().getSystemColor(SWT.COLOR_WHITE));
					}
				});
				ICard card = new Card(from, cardConfig, CardDataStoreFixture.url, CardDataStoreFixture.data1a);
				return card.getComposite();
			}
		});
	}

	@Override
	public Table getTable() {
		return content.table.getTable();
	}

	@Override
	public void addMenuDetectListener(Listener listener) {
		content.table.getTable().addListener(SWT.MenuDetect, listener);
	}

}