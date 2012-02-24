/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors.internal;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.internal.CardTable;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.DataWithOkCancelComposite;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.IValueEditor;

public class CardEditor implements IValueEditor, ICardData {

	static class CardEditorComposite extends DataWithOkCancelComposite<Table>  {

		private final CardTable cardTable;
		private final ICardEditorCallback callback;
		private final ICardData cardData;

		public CardEditorComposite(Composite parent, String titleString, final ICardData cardData, final ICardEditorCallback callback) {
			super(parent, cardData.getCardConfig(),cardData.cardType(), titleString);
			this.cardData = cardData;
			this.callback = callback;
			CardConfig cardConfig = getCardConfig();
			cardTable = new CardTable(getInnerBody(), cardConfig.withStyleAndSelection(cardConfig.cardStyle, true), getTitleSpec(), cardData.cardType(), cardData.data());

			Control firstEditor = null;
			for (TableItem item : cardTable.getTable().getItems()) {
				final TableEditor editor = new TableEditor(cardTable.getTable());
				editor.horizontalAlignment = SWT.LEFT;
				editor.grabHorizontal = true;
				editor.minimumWidth = 40;
				Text newEditor = new Text(cardTable.getTable(), SWT.NONE);
				if (firstEditor == null)
					firstEditor = newEditor;
				String text = Strings.nullSafeToString(cardData.data().get(item.getData()));
				newEditor.setText(text);
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, 1);
				newEditor.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent e) {
						Text text = (Text) editor.getEditor();
						TableItem item = editor.getItem();
						String key = (String) item.getData();
						String newValue = text.getText();
						cardTable.setNewValue(key, newValue);
						cardData.valueChanged(key, newValue);
						getFooter().setOkEnabled(callback.canOk(cardData.data()));
					}
				});
			}
			if (firstEditor != null)
				firstEditor.forceFocus();

		}

		@Override
		protected void ok() {
			callback.ok(cardData);
		}

		@Override
		protected void cancel() {
			callback.ok(cardData);

		}

		@Override
		public Table getEditor() {
			return cardTable.getTable();
		}


	}

	private final CardEditorComposite content;
	private final String cardType;
	private final CardConfig cardConfig;
	private final String url;
	private final Map<String, Object> data;

	public CardEditor(Composite parent, CardConfig cardConfig, String title, String cardType, String url, Map<String, Object> initialData, ICardEditorCallback callback) {
		this.cardConfig = cardConfig;
		this.cardType = cardType;
		this.url = url;
		this.data = cardConfig.modify(url, Maps.with(initialData, CardConstants.slingResourceType, cardType));
		content = new CardEditorComposite(parent, title, this, callback);
		content.getFooter().setOkEnabled(callback.canOk(data));
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
	public CardConfig getCardConfig() {
		return cardConfig;
	}

	@Override
	public String cardType() {
		return cardType;
	}

	@Override
	public void valueChanged(String key, Object newValue) {
		data.put(key, newValue);
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public Map<String, Object> data() {
		return data;
	}

}