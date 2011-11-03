package org.softwareFm.card.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.IMutableCardDataStore;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.details.IDetailsFactoryCallback;
import org.softwareFm.card.navigation.NavTitle;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class TextEditor implements IHasComposite {

	private final TextEditorComposite content;

	static class TextEditorComposite extends Composite {

		public NavTitle titleLabel;
		private final Text text;
		private final String originalValue;
		private final OkCancel okCancel;
		private final CardConfig cardConfig;

		public TextEditorComposite(Composite parent, int style, final CardConfig cardConfig, final String url, final String key, Object initialValue, final IDetailsFactoryCallback callback) {
			super(parent, style);
			this.cardConfig = cardConfig;
			KeyValue keyValue = new KeyValue(key, initialValue);
			String name = Functions.call(cardConfig.nameFn, keyValue);
			titleLabel = new NavTitle(this, cardConfig, name, "");
			setBackground(new Color(getDisplay(), 255, 255, 255));

			text = new Text(this, SWT.WRAP | SWT.BORDER);
			originalValue = Functions.call(cardConfig.valueFn, keyValue);
			text.setText(originalValue);
			okCancel = new OkCancel(this, cardConfig, new Runnable() {
				@Override
				public void run() {
					IMutableCardDataStore cardDataStore = (IMutableCardDataStore) cardConfig.cardDataStore;
					cardDataStore.put(url, Maps.<String, Object> makeMap(key, text.getText()), callback);
					TextEditorComposite.this.dispose();

				}
			}, new Runnable() {
				@Override
				public void run() {
					TextEditorComposite.this.dispose();
				}
			});

			updateEnabledStatusOfButtons();
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					updateEnabledStatusOfButtons();
				}
			});
		}

		@Override
		public void setLayout(Layout layout) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			Point textSize = text.computeSize(wHint, hHint);
			Point okCancelSize = okCancel.getControl().computeSize(wHint, hHint);
			int height = cardConfig.titleHeight + textSize.y + okCancelSize.y;
			int width = getParent().getClientArea().width;
			return new Point(width, height);
		}

		@Override
		public void layout(boolean changed) {
			Rectangle clientArea = getClientArea();
			int width = clientArea.width - cardConfig.leftMargin - cardConfig.rightMargin;
			titleLabel.getControl().setSize(width, cardConfig.titleHeight);
			int startY = clientArea.y + cardConfig.topMargin;
			int x = clientArea.x + cardConfig.leftMargin;

			titleLabel.getControl().setLocation(x, startY);

			int textY = startY + cardConfig.titleHeight;
			text.setLocation(x, textY);
			int textHeight = text.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			text.setSize(width, textHeight);

			Control okCancelControl = okCancel.getControl();
			okCancelControl.pack();
			int okCancelWidth = okCancelControl.getSize().x;
			int okCancelY = textY + textHeight;
			okCancelControl.setLocation(x + width - okCancelWidth, okCancelY);

		}

		private void updateEnabledStatusOfButtons() {
			okCancel.setOkEnabled(!originalValue.equals(text.getText()));
		}
	}

	public TextEditor(Composite parentComposite, CardConfig cardConfig, String url, String key, Object value, IDetailsFactoryCallback callback) {
		content = new TextEditorComposite(parentComposite, SWT.NULL, cardConfig, url, key, value, callback);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public Text getText() {
		return content.text;
	}

	public static void main(String[] args) {
		Swts.displayNoLayout(TextEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = new CardConfig(ICardFactory.Utils.cardFactory(), CardDataStoreFixture.rawCardStore());
				TextEditor textEditor = new TextEditor(from, cardConfig, "someUrl", "key", "value", IDetailsFactoryCallback.Utils.resizeAfterGotData());
				Swts.resizeMeToParentsSize(textEditor.getControl());
				textEditor.content.layout();
				Swts.layoutDump(from);
				Swts.resizeMeToParentsSizeWithLayout(textEditor);
				return textEditor.content;
			}
		});
	}

}
