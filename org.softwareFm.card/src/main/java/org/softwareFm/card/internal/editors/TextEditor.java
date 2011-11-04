package org.softwareFm.card.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.internal.details.IDetailsFactoryCallback;
import org.softwareFm.card.internal.details.TitleSpec;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;

public class TextEditor implements IHasComposite {

	private final TextEditorComposite content;

	static class TextEditorComposite extends ValueEditorComposite<Text> {

		public TextEditorComposite(Composite parent, int style, final CardConfig cardConfig, final String url, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
			super(parent, style, cardConfig, url, key, initialValue, titleSpec, callback);
		
		}

		@Override
		protected void updateEnabledStatusOfButtons() {
			okCancel.setOkEnabled(!originalValue.equals(getValue()));
		}

		@Override
		protected Text makeEditorControl(Composite parent, String originalValue) {
			Text result = new Text(parent, SWT.BORDER);
			result.setText(originalValue);
			return result;
		}

		@Override
		protected boolean useAllHeight() {
			return false;
		}

		@Override
		protected String getValue() {
			return editorControl.getText();
		}
	}

	public TextEditor(Composite parentComposite, CardConfig cardConfig, String url, String key, Object value, IDetailsFactoryCallback callback, TitleSpec titleSpec) {
		content = new TextEditorComposite(parentComposite, SWT.NULL, cardConfig, url, key, value, titleSpec, callback);
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
		return content.editorControl;
	}

	public static void main(String[] args) {
		Swts.displayNoLayout(TextEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = new CardConfig(ICardFactory.Utils.cardFactory(), CardDataStoreFixture.rawCardStore());
				TextEditor textEditor = new TextEditor(from, cardConfig, "someUrl", "key", "value", IDetailsFactoryCallback.Utils.resizeAfterGotData(), TitleSpec.noTitleSpec(from.getBackground()));
				Swts.resizeMeToParentsSize(textEditor.getControl());
				textEditor.content.layout();
				Swts.layoutDump(from);
				Swts.resizeMeToParentsSizeWithLayout(textEditor);
				return textEditor.content;
			}
		});
	}

}
