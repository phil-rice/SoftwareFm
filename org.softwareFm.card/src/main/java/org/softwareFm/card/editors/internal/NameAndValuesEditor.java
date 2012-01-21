/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.editors.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.editors.ICardEditorCallback;
import org.softwareFm.card.editors.INamesAndValuesEditor;
import org.softwareFm.card.editors.IValueComposite;
import org.softwareFm.card.editors.NameAndValueData;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.card.title.TitleWithTitlePaintListener;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class NameAndValuesEditor implements INamesAndValuesEditor {

	static class NameAndValuesEditorComposite extends Composite implements IValueComposite<Composite> {

		private final CardConfig cardConfig;
		private final OkCancel okCancel;
		private final TitleWithTitlePaintListener titleWithTitlePaintListener;
		private final ValueEditorBodyComposite body;
		private final SashForm editing;
		private final ICardEditorCallback callback;
		private final Control firstChild;

		public NameAndValuesEditorComposite(Composite parent, String titleString, final ICardData cardData, List<NameAndValueData> nameAndValueData, final ICardEditorCallback strategy) {
			super(parent, SWT.NULL);
			this.callback = strategy;
			this.cardConfig = cardData.getCardConfig();
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, cardData);

			titleWithTitlePaintListener = new TitleWithTitlePaintListener(this, cardConfig, titleSpec, titleString, "initialTooltip");
			body = new ValueEditorBodyComposite(this, cardConfig, titleSpec);
			editing = Swts.newSashForm(body.innerBody, SWT.HORIZONTAL, "editing");
			Composite labels = Swts.newComposite(editing, SWT.NULL, "Labels");
			Composite values = Swts.newComposite(editing, SWT.NULL, "value");
			labels.setBackground(titleSpec.background);
			values.setBackground(titleSpec.background);

			for (NameAndValueData data : nameAndValueData) {
				Label label = INamesAndValuesEditor.Utils.label(labels, cardConfig, cardData.cardType(), data.key);
				label.setBackground(titleSpec.background);
				Control editor = Functions.call(data.editorCreator, values);
				setInitialTextAndAddModified(cardData, editor, data.key);
			}
			Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(labels, cardConfig.editorIndentY);
			Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(values, cardConfig.editorIndentY);

			for (int i = 0; i < values.getChildren().length; i++) {
				Control value = values.getChildren()[i];
				if (value instanceof StyledText) {
					Control label = labels.getChildren()[i];
					Swts.Grid.addGrabVerticalToGridData(label, false);
					Swts.Grid.addGrabVerticalToGridData(value, true);
				}
			}

			editing.setWeights(new int[] { 1, 3 });

			IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, null);
			okCancel = new OkCancel(body.innerBody, resourceGetter, new Runnable() {
				@Override
				public void run() {
					strategy.ok(cardData);
				}
			}, new Runnable() {
				@Override
				public void run() {
					strategy.cancel(cardData);
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
			assert nameAndValueData.size() > 0;
			firstChild = values.getChildren()[0];
			firstChild.forceFocus();
		}

		@Override
		public boolean forceFocus() {
			boolean gotFocus = firstChild.forceFocus();
			return gotFocus;
		}

		private void setInitialTextAndAddModified(ICardData cardData, Control editor, String key) {
			if (editor instanceof Text)
				setInitialTextAndAddModified(cardData, (Text) editor, key);
			else if (editor instanceof StyledText)
				setInitialTextAndAddModified(cardData, (StyledText) editor, key);
			else
				throw new IllegalArgumentException(editor.getClass().getName());

		}

		private void setInitialTextAndAddModified(final ICardData cardData, final Text text, final String key) {
			String initial = Strings.nullSafeToString(cardData.data().get(key));
			text.setText(initial);
			text.setSelection(initial.length());
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					cardData.valueChanged(key, text.getText());
					okCancel.setOkEnabled(callback.canOk(cardData.data()));
				}
			});
			addListeners(text);
		}

		private void addListeners(Text text) {
			text.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (okCancel.isOkEnabled())
						okCancel.ok();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					if (okCancel.isOkEnabled())
						okCancel.ok();
				}
			});
			text.addKeyListener(new KeyListener() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.keyCode == SWT.ESC)
						okCancel.cancel();
				}

				@Override
				public void keyPressed(KeyEvent e) {
				}
			});
		}

		private void setInitialTextAndAddModified(final ICardData cardData, final StyledText text, final String key) {
			text.setText(Strings.nullSafeToString(cardData.data().get(key)));
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					cardData.valueChanged(key, text.getText());
					okCancel.setOkEnabled(callback.canOk(cardData.data()));
				}
			});
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
		public Composite getEditor() {
			return editing;
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

	private final NameAndValuesEditorComposite content;
	private final CardConfig cardConfig;
	private final String url;
	private final Map<String, Object> data;
	private final String cardType;

	public NameAndValuesEditor(Composite parent, CardConfig cardConfig, String cardType, String title, String url, Map<String, Object> initialData, List<NameAndValueData> nameAndValueData, ICardEditorCallback callback) {
		this.cardConfig = cardConfig;
		this.cardType = cardType;
		this.url = url;
		this.data = cardConfig.modify(url, Maps.with(initialData, CardConstants.slingResourceType, cardType));
		content = new NameAndValuesEditorComposite(parent, title, this, nameAndValueData, callback);
		content.okCancel.setOkEnabled(callback.canOk(data));
		content.setLayout(new ValueEditorLayout());
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
		return (Composite) content.okCancel.getControl();
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

	public static void main(String[] args) {
		Swts.Show.display(NameAndValuesEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				Map<String, Object> data = Maps.stringObjectMap("title", "Some title", "description", "Some Description", "content", "public void testThis(){\n  fail();\n}\n");
				final String cardType = "";
				NameAndValuesEditor editor = new NameAndValuesEditor(from, cardConfig, cardType, "someTitle", "tutorial", data, Arrays.asList(//
						INamesAndValuesEditor.Utils.text(cardConfig, cardType, "email"),//
						INamesAndValuesEditor.Utils.text(cardConfig, cardType, "password"),//
						INamesAndValuesEditor.Utils.text(cardConfig, cardType, "confirmPassword"),//
						INamesAndValuesEditor.Utils.styledText(cardConfig, cardType, "address"),//
						INamesAndValuesEditor.Utils.styledText(cardConfig, cardType, "comment")), new ICardEditorCallback() {

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
				return editor.getComposite();
			}
		});
	}
}