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
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.strings.Strings;

public class Card implements ICard {
	private Future<?> populateFuture;
	private final List<KeyValue> data;
	private final String url;

	private final Table table;
	private final TableColumn nameColumn;
	private final TableColumn valueColumn;
	private final List<ILineSelectedListener> lineSelectedListeners = new CopyOnWriteArrayList<ILineSelectedListener>();

	private boolean reported;

	public Card(Composite parent, int style, final boolean allowSelection, ICardDataStore cardDataStore, final String url, final List<KeyValue> data) {
		this.url = url;
		this.data = data;
		this.table = new Table(parent, style);
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
			tableItem.setText(new String[] { keyValue.key, Strings.nullSafeToString(keyValue.value) });
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
				if (!allowSelection)
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
	public Future<?> getPopulateFuture() {
		return populateFuture;
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

		Swts.display(Card.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				ICard card = cardFactory.makeCard(from, SWT.FULL_SELECTION | SWT.NO_SCROLL, true, CardDataStoreFixture.url, CardDataStoreFixture.data1a);
				return card.getComposite();
			}
		});
	}

}
