/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

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
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.swt.card.CardOutlinePaintListener;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardValueChangedListener;
import org.softwareFm.swt.card.ILineSelectedListener;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.title.TitleSpec;

public class Card implements ICard {

	private final static boolean profile = false;

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
			Table tableControl = (Table) card.table.getControl();
			tableControl.setBounds(ca);
			Point size = tableControl.getSize();
			if (size.x == 0)
				return;
			CardConfig cardConfig = card.cardConfig;
			ICard.Utils.setCardTableColumnWidths(tableControl, cardConfig);
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

			CardOutlinePaintListener cardOutlinePaintListener = new CardOutlinePaintListener(cardConfig, Callables.value(titleSpec));
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
	private final String cardType;

	public Card(Composite parent, final CardConfig cardConfig, final String url, Map<String, Object> rawData) {
		this.cardConfig = cardConfig;
		this.cardType = (String) rawData.get(CardConstants.slingResourceType);
		final TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, this);
		content = new CardComposite(parent, cardConfig, url, rawData, cardType, titleSpec);
		content.table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				long startTime = profile ? System.currentTimeMillis() : 0;
				int index = content.table.getSelectionIndex();
				String key = index == -1 ? null : (String) content.table.getItem(index).getData();
				notifyLineSelectedListeners(key);
				if (!cardConfig.allowSelection)
					content.table.deselectAll();
				if (profile)
					System.out.println(Card.class.getSimpleName() + ".widgetSelected: " + (System.currentTimeMillis() - startTime));
			}

		});
		addMenuDetectListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Menu menu = new Menu(getControl());
				String popupMenuId = Functions.call(cardConfig.popupMenuIdFn, Card.this);
				cardConfig.popupMenuService.contributeTo(popupMenuId, event, menu, Card.this);
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
			long startTime = profile ? System.currentTimeMillis() : 0;
			lineSelectedListener.selected(Card.this, key, content.data.get(key));
			if (profile)
				System.out.println("    " + lineSelectedListener + " took: " + (System.currentTimeMillis() - startTime));
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

	@Override
	public Table getTable() {
		return content.table.getTable();
	}

	@Override
	public void addMenuDetectListener(Listener listener) {
		content.table.getTable().addListener(SWT.MenuDetect, listener);
	}

}