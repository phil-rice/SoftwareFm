package org.softwareFm.card.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.utilities.functions.IFunction1;

public class StyledTextEditor implements IHasComposite {

	private final TextEditorComposite content;

	static class TextEditorComposite extends ValueEditorComposite<StyledText> {

		public TextEditorComposite(Composite parent, int style, final CardConfig cardConfig, final String url, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
			super(parent, style, cardConfig, url, key, initialValue, titleSpec, callback);
		}

		@Override
		protected void updateEnabledStatusOfButtons() {
			okCancel.setOkEnabled(!originalValue.equals(getValue()));
		}

		@Override
		protected StyledText makeEditorControl(Composite parent, String originalValue) {
			StyledText result = new StyledText(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
			result.setText(originalValue);
			result.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					updateEnabledStatusOfButtons();
				}
			});
			result.setFocus();
			return result;
		}

		@Override
		public boolean useAllHeight() {
			return true;
		}

		@Override
		protected String getValue() {
			return editorControl.getText();
		}
	}

	public StyledTextEditor(Composite parentComposite, CardConfig cardConfig, String url, String key, Object value, IDetailsFactoryCallback callback, TitleSpec titleSpec) {
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

	public StyledText getText() {
		return content.editorControl;
	}

	public TitleSpec getTitleSpec() {
		return content.getTitleSpec();
	}

	public static void main(String[] args) {
		Show.displayNoLayout(StyledTextEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				StyledTextEditor textEditor = new StyledTextEditor(from, cardConfig, "someUrl", "key", "value", IDetailsFactoryCallback.Utils.resizeAfterGotData(), TitleSpec.noTitleSpec(from.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN)));
				textEditor.getComposite().setLayout(new ValueEditorLayout());
				Size.resizeMeToParentsSizeWithLayout(textEditor);
				return textEditor.content;
			}
		});
	}

}
