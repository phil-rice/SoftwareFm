package org.softwareFm.card.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

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
import org.softwareFm.utilities.functions.IFunction1;

public class Card implements ICard {
	private Future<?> populateFuture;
	private final List<KeyValue> data;
	private final String url;

	private final Table table;
	private final TableColumn nameColumn;
	private final TableColumn valueColumn;
	private final List<ILineSelectedListener> lineSelectedListeners = new CopyOnWriteArrayList<ILineSelectedListener>();
	private final CardConfig cardConfig;

	public Card(Composite parent, final CardConfig cardConfig, final String url, final List<KeyValue> data) {
		this.cardConfig = cardConfig;
		this.url = url;
		this.data = data;
		this.table = new Table(parent, cardConfig.style);
		this.nameColumn = new TableColumn(table, SWT.NONE);
		this.valueColumn = new TableColumn(table, SWT.NONE);

		table.setHeaderVisible(true);
		nameColumn.setText("Name");
		valueColumn.setText("Value");
		nameColumn.setWidth(100);
		valueColumn.setWidth(100);
		table.setDragDetect(true);
		for (KeyValue keyValue : data) {
			TableItem tableItem = new TableItem(table, SWT.NULL);
			String displayValue = keyValue.value instanceof List ? "Children: " + ((List<?>) keyValue.value).size() : keyValue.value.toString();
			tableItem.setText(new String[] { keyValue.key, displayValue });
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
				KeyValue keyValue = data.get(index);
				for (ILineSelectedListener lineSelectedListener : lineSelectedListeners) {
					lineSelectedListener.selected(keyValue);
				}
				if (!cardConfig.allowSelection)
					table.deselectAll();
			}
		});
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

	public static void main(String[] args) {
		final ICardDataStore cardDataStore = CardDataStoreFixture.rawCardStore();
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactory(cardDataStore);
		final CardConfig cardConfig = new CardConfig(cardFactory, cardDataStore);

		Swts.display(Card.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ICard card = cardFactory.makeCard(from, cardConfig, CardDataStoreFixture.url, CardDataStoreFixture.data1a);
				return card.getComposite();
			}
		});
	}

}
