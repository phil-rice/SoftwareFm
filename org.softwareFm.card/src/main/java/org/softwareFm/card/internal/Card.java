package org.softwareFm.card.internal;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardValueChangedListener;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class Card implements ICard {
	/** Holds strategies and values to control how the card is displayed */
	private final CardConfig cardConfig;
	/** The original data passed to you. Kept for debugging / tests */
	private final Map<String, Object> rawData;
	/** the current view of the data. It may change as more information is acquired from the server. It will often have been aggregated */
	private final Map<String, Object> data;

	/** The url that this card is a representation of */
	private final String url;

	/** The gui component that displays the data */
	final Table table;
	private final TableColumn nameColumn;
	private final TableColumn valueColumn;

	/** If a line is selected by the user, then these listeners are informed */
	private final List<ILineSelectedListener> lineSelectedListeners = new CopyOnWriteArrayList<ILineSelectedListener>();
	/** Controls access to data */
	private final Object lock = new Object();

	/** What type of card is this? Examples are the strings 'collection', 'group', 'artifact' */
	private String cardType;
	private final List<ICardValueChangedListener> valueChangedListeners = new CopyOnWriteArrayList<ICardValueChangedListener>();

	public Card(Composite parent, final CardConfig cardConfig, final String url, Map<String, Object> rawData) {
		this.cardConfig = cardConfig;
		this.url = url;
		this.rawData = rawData;
		this.table = new Table(parent, cardConfig.cardStyle );
		this.nameColumn = new TableColumn(table, SWT.NONE);
		this.valueColumn = new TableColumn(table, SWT.NONE);
		this.cardType = (String) rawData.get(CardConstants.slingResourceType);
		data = cardConfig.modify(url, rawData);

//		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		nameColumn.setText(IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "card.name.title"));
		valueColumn.setText(IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "card.value.title"));
		nameColumn.setWidth(100);
		valueColumn.setWidth(100);
		table.setDragDetect(true);
		final TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, this);
		table.setBackground(titleSpec.background);
		int i = 0;
		for (Entry<String, Object> entry : data.entrySet()) {
			KeyValue keyValue = new KeyValue(entry.getKey(), entry.getValue());
			if (!Functions.call(cardConfig.hideFn, keyValue)) { // Have to make an object here, but we are decoupling how we store the data, and the JVM probably optimises it away anyway
				TableItem tableItem = new TableItem(table, SWT.NULL);
				setTableItem(i, cardConfig, tableItem, keyValue);
			}
			i++;
		}

		nameColumn.pack();
		valueColumn.pack();
		table.pack();
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
				if (index == -1)
					return;
				String key = (String) table.getItem(index).getData();
				for (ILineSelectedListener lineSelectedListener : lineSelectedListeners) {
					lineSelectedListener.selected(Card.this, key, data.get(key));
				}
				if (!cardConfig.allowSelection)
					table.deselectAll();
			}
		});
		table.addPaintListener(new CardOutlinePaintListener(titleSpec, cardConfig, table));
		table.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				layout();
			}
		});
	}

	public void layout() {
		Point size = table.getSize();
		if (size.x == 0)
			return;
		nameColumn.pack();
		int idealNameWidth = nameColumn.getWidth();
		int newNameWidth = (size.x * cardConfig.cardNameWeight) / (cardConfig.cardNameWeight + cardConfig.cardValueWeight);
		int maxNameValue = (int) (idealNameWidth * cardConfig.cardMaxNameSizeRatio);
		if (newNameWidth > maxNameValue)
			newNameWidth = maxNameValue;
		int newValueWidth = size.x - newNameWidth-1;
		nameColumn.setWidth(newNameWidth);
		valueColumn.setWidth(newValueWidth);
	}

	private void setTableItem(int i, final CardConfig cardConfig, TableItem tableItem, KeyValue keyValue) {
		// System.out.println("Setting table item[" + i + "]: " + keyValue);
		String displayValue = Functions.call(cardConfig.valueFn, keyValue);
		String name = Functions.call(cardConfig.nameFn, keyValue);
		tableItem.setText(new String[] { name, displayValue });
		tableItem.setData(keyValue.key);
	}

	@Override
	public void valueChanged(String key, Object newValue) {
		int index = findTableItem(key);
		synchronized (lock) {
			try {
				TableItem item = table.getItem(index);
				data.put(key, newValue);
				setTableItem(index, cardConfig, item, new KeyValue(key, newValue));
				table.redraw();
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(MessageFormat.format(CardConstants.exceptionChangingValue, key, index, newValue));
			}
		}
		for (ICardValueChangedListener listener : valueChangedListeners)
			listener.valueChanged(this, key, newValue);
	}

	private int findTableItem(String key) {
		for (int i = 0; i < table.getItemCount(); i++)
			if (table.getItem(i).getData() == key)
				return i;
		throw new RuntimeException(MessageFormat.format(CardConstants.cannotFindTableItemWithKey, key));
	}

	@Override
	public Composite getComposite() {
		return table;
	}

	@Override
	public Control getControl() {
		return table;
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
	public CardConfig cardConfig() {
		return cardConfig;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public Map<String, Object> data() {
		synchronized (lock) {
			return Maps.copyMap(data);
		}
	}

	public Map<String, Object> rawData() {
		return rawData;
	}

	public static void main(String[] args) {
		final ICardDataStore cardDataStore = CardDataStoreFixture.rawCardStore();
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactory();

		Show.display(Card.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardConfig cardConfig = new CardConfig(cardFactory, cardDataStore).withTitleSpecFn(new IFunction1<ICard, TitleSpec>() {
					@Override
					public TitleSpec apply(ICard card) throws Exception {
						return TitleSpec.noTitleSpec(from.getDisplay().getSystemColor(SWT.COLOR_WHITE));
					}
				});
				ICard card = new Card(from, cardConfig, CardDataStoreFixture.url, CardDataStoreFixture.data1a);
				return card.getComposite();
			}
		});
	}

	@Override
	public String cardType() {
		return cardType;
	}

	@Override
	public String toString() {
		return "Card [url=" + url + ", cardType=" + cardType + ", data=" + data + "]";
	}

}
