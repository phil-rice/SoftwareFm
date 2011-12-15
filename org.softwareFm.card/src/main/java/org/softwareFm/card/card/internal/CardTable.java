/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICardTable;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardTable implements ICardTable {

	private final Table table;
	private final TableColumn nameColumn;
	private final TableColumn valueColumn;
	private final String cardType;
	private final CardConfig cardConfig;

	public CardTable(Composite parent, CardConfig cardConfig, TitleSpec titleSpec, String cardType, Map<String, Object> data) {
		this.cardConfig = cardConfig;
		this.cardType = cardType;
		this.table = new Table(parent, cardConfig.cardStyle | SWT.V_SCROLL);
		table.setLinesVisible(true);
		this.nameColumn = new TableColumn(table, SWT.NONE);
		this.valueColumn = new TableColumn(table, SWT.NONE);

		nameColumn.setText(IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, "card.name.title"));
		valueColumn.setText(IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, "card.value.title"));
		nameColumn.setWidth(100);
		valueColumn.setWidth(100);
		table.setDragDetect(true);
		table.setBackground(titleSpec.background);
		int i = 0;
		for (Entry<String, Object> entry : data.entrySet()) {
			LineItem lineItem = new LineItem(cardType, entry.getKey(), entry.getValue());
			if (!Functions.call(cardConfig.hideFn(), lineItem)) { // Have to make an object here, but we are decoupling how we store the data, and the JVM probably optimises it away anyway
				TableItem tableItem = new TableItem(table, SWT.NULL);
				setTableItem(i, cardConfig, tableItem, lineItem);
			}
			i++;
		}

		nameColumn.pack();
		valueColumn.pack();
		table.pack();
	}

	public TableColumn getNameColumn() {
		return nameColumn;
	}

	public TableColumn getValueColumn() {
		return valueColumn;
	}

	public void setNewValue(String key, Object newValue) {
		try {
			int index = findTableItem(key);
			TableItem item = table.getItem(index);
			setTableItem(index, cardConfig, item, new LineItem(cardType, key, newValue));
			table.redraw();
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(MessageFormat.format(CardConstants.exceptionChangingValue, key, newValue));
		}
	}

	private int findTableItem(String key) {
		for (int i = 0; i < table.getItemCount(); i++)
			if (table.getItem(i).getData() == key)
				return i;
		throw new RuntimeException(MessageFormat.format(CardConstants.cannotFindTableItemWithKey, key));
	}

	public void setTableItem(int i, final CardConfig cardConfig, TableItem tableItem, LineItem lineItem) {
		// System.out.println("Setting table item[" + i + "]: " + keyValue);
		String displayValue = Functions.call(cardConfig.valueFn(), lineItem);
		String name = Functions.call(cardConfig.nameFn(), lineItem);
		tableItem.setText(new String[] { name, displayValue });
		Image image = Functions.call(cardConfig.iconFn, lineItem);
		tableItem.setImage(0, image);
		tableItem.setData(lineItem.key);
	}

	@Override
	public Control getControl() {
		return table;
	}

	public void addSelectionListener(SelectionListener listener) {
		table.addSelectionListener(listener);
	}

	public int getSelectionIndex() {
		return table.getSelectionIndex();
	}

	public TableItem getItem(int index) {
		return table.getItem(index);
	}

	public void redraw() {
		table.redraw();
	}

	public int getItemCount() {
		return table.getItemCount();
	}

	public void deselectAll() {
		table.deselectAll();
	}

	@Override
	public Table getTable() {
		return table;
	}

}