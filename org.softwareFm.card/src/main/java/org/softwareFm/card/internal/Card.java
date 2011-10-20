package org.softwareFm.card.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class Card implements ICard {
	private final List<KeyValue> data;
	private final String url;

	private final Table table;
	private final TableColumn nameColumn;
	private final TableColumn valueColumn;
	private final List<ILineSelectedListener> lineSelectedListeners = new CopyOnWriteArrayList<ILineSelectedListener>();
	private final CardConfig cardConfig;
	private final Map<String, Object> rawData;

	public Card(Composite parent, final CardConfig cardConfig, final String url, Map<String, Object> rawData) {
		this.cardConfig = cardConfig;
		this.url = url;
		this.rawData = rawData;
		this.table = new Table(parent, cardConfig.style);
		this.nameColumn = new TableColumn(table, SWT.NONE);
		this.valueColumn = new TableColumn(table, SWT.NONE);
		data = Functions.call(cardConfig.aggregator, rawData);
		Collections.sort(data, cardConfig.comparator);

		table.setHeaderVisible(false);
		nameColumn.setText(IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "card.name.title"));
		valueColumn.setText(IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "card.value.title"));
		nameColumn.setWidth(100);
		valueColumn.setWidth(3000);
		table.setDragDetect(true);
		int i = 0;
		for (KeyValue keyValue : data) {
			if (!Functions.call(cardConfig.hideFn, keyValue)) {
				TableItem tableItem = new TableItem(table, SWT.NULL);
				setTableItem(cardConfig, tableItem, keyValue);
				tableItem.setData(i);
			}
			i++;
		}

		nameColumn.pack();
		table.pack();
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
				if (index == -1)
					return;
				Integer dataIndex = (Integer) table.getItem(index).getData();
				KeyValue keyValue = data.get(dataIndex);
				for (ILineSelectedListener lineSelectedListener : lineSelectedListeners) {
					lineSelectedListener.selected(keyValue);
				}
				if (!cardConfig.allowSelection)
					table.deselectAll();
			}
		});
	}

	private void setTableItem(final CardConfig cardConfig, TableItem tableItem, KeyValue keyValue) {
		String displayValue = Functions.call(cardConfig.valueFn, keyValue);
		String name = Functions.call(cardConfig.nameFn, keyValue);
		tableItem.setText(new String[] { name, displayValue });
	}

	@Override
	public void valueChanged(final KeyValue keyValue, final Object newValue) {
		int index = data.indexOf(keyValue);
		TableItem item = table.getItem(index);
		data.set(index, new KeyValue(keyValue.key, newValue));
		setTableItem(cardConfig, item, keyValue);
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
	public CardConfig cardConfig() {
		return cardConfig;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public List<KeyValue> data() {
		return data;
	}

	@Override
	public Map<String, Object> rawData() {
		return rawData;
	}

	public static void main(String[] args) {
		final ICardDataStore cardDataStore = CardDataStoreFixture.rawCardStore();
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactory();

		final CardConfig cardConfig = new CardConfig(cardFactory, cardDataStore);

		Swts.display(Card.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ICard card = new Card(from, cardConfig, CardDataStoreFixture.url, CardDataStoreFixture.data1a);
				return card.getComposite();
			}
		});
	}

}
