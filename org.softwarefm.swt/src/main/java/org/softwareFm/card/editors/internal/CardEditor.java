/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.editors.internal;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.card.internal.CardTable;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.editors.ICardEditorCallback;
import org.softwareFm.card.editors.IValueComposite;
import org.softwareFm.card.editors.IValueEditor;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.card.title.TitleWithTitlePaintListener;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.display.swt.Swts;

public class CardEditor implements IValueEditor, ICardData {

	static class CardEditorComposite extends Composite implements IValueComposite<Table> {

		private final CardConfig cardConfig;
		private final OkCancel okCancel;
		private final CardTable cardTable;
		private final TitleWithTitlePaintListener titleWithTitlePaintListener;
		private final ValueEditorBodyComposite body;

		public CardEditorComposite(Composite parent, String titleString, final ICardData cardData, final ICardEditorCallback callback) {
			super(parent, SWT.NULL);
			this.cardConfig = cardData.getCardConfig();
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, cardData);
			titleWithTitlePaintListener = new TitleWithTitlePaintListener(this, cardConfig, titleSpec, titleString, "initialTooltip");
			body = new ValueEditorBodyComposite(this, cardConfig, titleSpec);

			cardTable = new CardTable(body.innerBody, cardConfig.withStyleAndSelection(cardConfig.cardStyle, true), titleSpec, cardData.cardType(), cardData.data());

			IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, null);
			okCancel = new OkCancel(body.innerBody, resourceGetter, cardConfig.imageFn, new Runnable() {
				@Override
				public void run() {
					callback.ok(cardData);
				}
			}, new Runnable() {
				@Override
				public void run() {
					callback.cancel(cardData);
				}
			});
			body.innerBody.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					int width = body.innerBody.getSize().x;
					int y = okCancel.getControl().getLocation().y - 1;
					e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GRAY));
					e.gc.drawLine(0, y, width, y);
				}
			});

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
						okCancel.setOkEnabled(callback.canOk(cardData.data()));
					}
				});
			}
			if (firstEditor != null)
				firstEditor.forceFocus();

		}

		@Override
		public CardConfig getCardConfig() {
			return cardConfig;
		}

		@Override
		public TitleWithTitlePaintListener getTitle() {
			return titleWithTitlePaintListener;
		}

		@Override
		public Composite getBody() {
			return body;
		}

		@Override
		public Composite getInnerBody() {
			return body.innerBody;
		}

		@Override
		public Table getEditor() {
			return cardTable.getTable();
		}

		@Override
		public OkCancel getOkCancel() {
			return okCancel;
		}

		@Override
		public boolean useAllHeight() {
			return true;
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
		content.okCancel.setOkEnabled(callback.canOk(data));
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

	public static void main(String[] args) {
		Swts.Show.display(CardEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				CardEditor editor = new CardEditor(from, cardConfig, "someTitle", "someUrl", "tutorial", CardDataStoreFixture.data1aWithP1Q2, new ICardEditorCallback() {

					@Override
					public void ok(ICardData cardData) {
						System.out.println("Ok: " + cardData.data());

					}

					@Override
					public void cancel(ICardData cardData) {
						System.out.println("Cancel: " + cardData.data());

					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						return true;
					}
				});
				editor.getComposite().setLayout(new ValueEditorLayout());
				return editor.getComposite();
			}
		});
	}

}