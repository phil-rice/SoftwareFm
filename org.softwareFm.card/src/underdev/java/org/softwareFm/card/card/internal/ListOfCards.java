/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

/* This file is part of SoftwareFm
 /* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.MapAsList;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class ListOfCards implements IHasControl {
	private final Table content;
	private Object value;
	private String sortOrder;
	private final CardConfig cardConfig;
	private List<String> keyOrder;
	private MapAsList mapAsList;

	public ListOfCards(Composite parent, CardConfig cardConfig) {
		this.cardConfig = cardConfig;
		content = new Table(parent, SWT.V_SCROLL | SWT.FULL_SELECTION);
		content.setHeaderVisible(true);
	}

	public Future<?> setKeyValue(final String rootUrl, String cardType, String key, Object value, final IDetailsFactoryCallback callback) {
		this.value = value;
		this.sortOrder = "key";
		String cardOrderAsString = IResourceGetter.Utils.getOr(cardConfig.resourceGetterFn, cardType, CardConstants.cardOrderKey, "");
		this.keyOrder = Strings.splitIgnoreBlanks(cardOrderAsString, ",");
		populateTable();
		content.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int selectionIndex = content.getSelectionIndex();
				if (selectionIndex != -1) {
					String key = (String) mapAsList.values.get(selectionIndex).get(mapAsList.keyIndex);
					callback.cardSelected(rootUrl + "/" + key);
				}

			}
		});
		callback.gotData(content);
		return null;
	}

	@SuppressWarnings("unchecked")
	private void populateTable() {
		if (content.isDisposed())
			return;
		content.removeAll();
		for (TableColumn column : content.getColumns())
			column.dispose();
		if (value instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) value;
			mapAsList = Maps.toMapAsList("key", map, sortOrder, keyOrder);
			for (final String title : mapAsList.titles) {
				TableColumn tableColumn = new TableColumn(content, SWT.NULL);
				tableColumn.setText(title);
				tableColumn.setWidth(100);
				tableColumn.addListener(SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event event) {
						sortOrder = title;
						populateTable();

					}
				});
			}
			for (List<Object> objects : mapAsList.values) {
				TableItem tableItem = new TableItem(content, SWT.NULL);
				for (int i = 0; i < objects.size(); i++) {
					tableItem.setText(i, Strings.nullSafeToString(objects.get(i)));
				}
			}
		}
	}

	@Override
	public Control getControl() {
		return content;
	}

	public static void main(String[] args) {
		Swts.Show.display(ListOfCards.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				ListOfCards result = new ListOfCards(composite, cardConfig);
				Swts.Size.resizeMeToParentsSize(result.getControl());
				Map<String, Object> data = Maps.stringObjectLinkedMap(//
						"1c", CardDataStoreFixture.data1a, //
						"1b", CardDataStoreFixture.data1b,//
						"2c", CardDataStoreFixture.data2a,//
						"2b", CardDataStoreFixture.data2b,//
						"2a", CardDataStoreFixture.data2c);
				result.setKeyValue("/someUrl", "cardType", "key", data, IDetailsFactoryCallback.Utils.noCallback());
				return composite;
			}
		});
	}
}