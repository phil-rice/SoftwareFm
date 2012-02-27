/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.LineItem;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.swt.Swts;

public class NameAndValuesEditor implements INamesAndValuesEditor {

	static class NameAndValuesEditorComposite extends DataWithOkCancelComposite<Composite> {

		private final SashForm editing;
		private final ICardEditorCallback callback;
		private final Control firstChild;
		private final ICardData cardData;
		private final ICardEditorCallback cardEditorCallback;

		public NameAndValuesEditorComposite(Composite parent, String titleString, final ICardData cardData, List<KeyAndEditStrategy> keyAndEditStrategy, final ICardEditorCallback strategy) {
			super(parent, cardData.getCardConfig(), cardData.cardType(), titleString);
			this.cardData = cardData;
			this.cardEditorCallback = strategy;
			this.callback = strategy;
			editing = Swts.newSashForm(getInnerBody(), SWT.HORIZONTAL, "editing");
			Composite labels = Swts.newComposite(editing, SWT.NULL, "Labels");
			Composite values = Swts.newComposite(editing, SWT.NULL, "value");
			labels.setBackground(getTitleSpec().background);
			values.setBackground(getTitleSpec().background);

			CardConfig cc = getCardConfig();
			for (KeyAndEditStrategy data : keyAndEditStrategy) {
				Label label = INamesAndValuesEditor.Utils.label(labels, cc, cardData.cardType(), data.key);
				label.setBackground(getTitleSpec().background);
				@SuppressWarnings("unchecked")
				IEditableControlStrategy<Control> editableControlStrategy = (IEditableControlStrategy<Control>) data.editableControlStrategy;
				Control editor = editableControlStrategy.createControl(values);
				String value = cc.valueFn.apply(cc, new LineItem(cardData.cardType(), data.key, cardData.data().get(data.key)));
				editableControlStrategy.populateInitialValue(editor, value);
				editableControlStrategy.whenModifed(editor, cardData, data.key, new Runnable() {
					@Override
					public void run() {
						getFooter().setOkEnabled(callback.canOk(cardData.data()));
					}
				});
				editableControlStrategy.addEnterEscapeListeners(getFooter(), editor);
			}
			Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(labels, cc.editorIndentY);
			Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(values, cc.editorIndentY);

			for (int i = 0; i < values.getChildren().length; i++) {
				Control value = values.getChildren()[i];
				if (value instanceof StyledText) {
					Control label = labels.getChildren()[i];
					Swts.Grid.addGrabVerticalToGridData(label, false);
					Swts.Grid.addGrabVerticalToGridData(value, true);
				}
			}

			editing.setWeights(new int[] { 1, 3 });

			getInnerBody().addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					int width = getInnerBody().getSize().x;
					int y = getFooter().getControl().getLocation().y - 1;
					e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_GRAY));
					e.gc.drawLine(0, y, width, y);
				}
			});
			assert keyAndEditStrategy.size() > 0;
			firstChild = values.getChildren()[0];
			firstChild.forceFocus();
		}

		@Override
		public boolean forceFocus() {
			boolean gotFocus = firstChild.forceFocus();
			return gotFocus;
		}

		@Override
		public Composite getEditor() {
			return editing;
		}

		@Override
		public boolean useAllHeight() {
			return true;
		}

		@Override
		protected void ok() {
			cardEditorCallback.ok(cardData);
		}

		@Override
		protected void cancel() {
			cardEditorCallback.cancel(cardData);
		}

	}

	private final NameAndValuesEditorComposite content;
	private final CardConfig cardConfig;
	private final String url;
	private final Map<String, Object> data;
	private final String cardType;

	public NameAndValuesEditor(Composite parent, CardConfig cardConfig, String cardType, String title, String url, Map<String, Object> initialData, List<KeyAndEditStrategy> keyAndEditStrategy, ICardEditorCallback callback) {
		this.cardConfig = cardConfig;
		this.cardType = cardType;
		this.url = url;
		this.data = cardConfig.modify(url, Maps.with(initialData, CardConstants.slingResourceType, cardType));
		content = new NameAndValuesEditorComposite(parent, title, this, keyAndEditStrategy, callback);
		content.getFooter().setOkEnabled(callback.canOk(data));
		content.setLayout(new DataCompositeWithFooterLayout());
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
	public void valueChanged(String key, Object newValue) {
		data.put(key, newValue);
	}

	@Override
	public Composite getButtonComposite() {
		return content.getFooter().getComposite();
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public Map<String, Object> data() {
		return data;
	}

	@Override
	public String cardType() {
		return cardType;
	}

}