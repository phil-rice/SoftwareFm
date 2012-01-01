/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.editors.internal;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.editors.ICardEditorCallback;
import org.softwareFm.card.editors.IValueComposite;
import org.softwareFm.card.editors.IValueEditor;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.card.title.TitleWithTitlePaintListener;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class SnippetEditor implements IValueEditor, ICardData {

	public static class SnippetEditorLayout extends ValueEditorLayout {

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			super.layout(composite, flushCache);
			SnippetEditorComposite c = (SnippetEditorComposite) composite;
			Rectangle descriptionBounds = c.descriptionText.getBounds();
			Rectangle ca = c.editing.getClientArea();
			int starty = descriptionBounds.y + descriptionBounds.height;
			int spacer = 5;
			c.contentText.setBounds(ca.x, starty + spacer, ca.width, (ca.height + ca.y) - starty - 2 * spacer);
		}
	}

	static class SnippetEditorComposite extends Composite implements IValueComposite<Composite> {

		private final CardConfig cardConfig;
		private final OkCancel okCancel;
		private final TitleWithTitlePaintListener titleWithTitlePaintListener;
		private final ValueEditorBodyComposite body;
		private final SashForm editing;
		final StyledText contentText;
		final Text descriptionText;
		final Text titleText;

		public SnippetEditorComposite(Composite parent, String titleString, final ICardData cardData, final ICardEditorCallback callback) {
			super(parent, SWT.NULL);
			this.cardConfig = cardData.getCardConfig();
			TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, cardData);
			titleWithTitlePaintListener = new TitleWithTitlePaintListener(this, cardConfig, titleSpec, titleString, "initialTooltip");
			body = new ValueEditorBodyComposite(this, cardConfig, titleSpec);
			editing = Swts.newSashForm(body.innerBody, SWT.HORIZONTAL, "editing");
			Composite labels = Swts.newComposite(editing, SWT.NULL, "Labels");
			new Label(labels, SWT.NULL).setText("Title");
			new Label(labels, SWT.NULL).setText("Description");
			new Label(labels, SWT.NULL).setText("Content");
			Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildren(labels);

			Composite values = Swts.newComposite(editing, SWT.NULL, "value");
			titleText = new Text(values, SWT.NULL);
			descriptionText = new Text(values, SWT.NULL);
			contentText = new StyledText(values, SWT.WRAP);

			setInitialTextAndAddModified(cardData, titleText, "title");
			setInitialTextAndAddModified(cardData, descriptionText, "description");
			setInitialTextAndAddModified(cardData, contentText, "content");
			Swts.Grid.addGrabHorizontalAndFillGridDataToAllChildren(values);
			editing.setWeights(new int[] { 1, 3 });

			IResourceGetter resourceGetter = Functions.call(cardConfig.resourceGetterFn, null);
			okCancel = new OkCancel(body.innerBody, resourceGetter, new Runnable() {
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
			titleText.forceFocus();

		}

		private void setInitialTextAndAddModified(final ICardData cardData, final Text text, final String key) {
			text.setText(Strings.nullSafeToString(cardData.data().get(key)));
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					cardData.valueChanged(key, text.getText());
				}
			});
		}

		private void setInitialTextAndAddModified(final ICardData cardData, final StyledText text, final String key) {
			text.setText(Strings.nullSafeToString(cardData.data().get(key)));
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					cardData.valueChanged(key, text.getText());
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

	private final SnippetEditorComposite content;
	private final CardConfig cardConfig;
	private final String url;
	private final Map<String, Object> data;

	public SnippetEditor(Composite parent, CardConfig cardConfig, String title, String url, Map<String, Object> initialData, ICardEditorCallback callback) {
		this.cardConfig = cardConfig;
		this.url = url;
		this.data = cardConfig.modify(url, Maps.with(initialData, CardConstants.slingResourceType, cardType()));
		content = new SnippetEditorComposite(parent, title, this, callback);
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
		return CardConstants.snippet;
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
		Swts.Show.display(SnippetEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				Map<String, Object> data = Maps.stringObjectMap("title", "Some title", "description", "Some Description", "content", "public void testThis(){\n  fail();\n}\n");
				SnippetEditor editor = new SnippetEditor(from, cardConfig, "someTitle", "tutorial", data, new ICardEditorCallback() {

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
				editor.getComposite().setLayout(new SnippetEditorLayout());
				return editor.getComposite();
			}
		});
	}

}