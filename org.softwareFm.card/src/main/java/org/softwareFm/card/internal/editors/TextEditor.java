package org.softwareFm.card.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.internal.details.IDetailsFactoryCallback;
import org.softwareFm.card.internal.details.TitleSpec;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;

public class TextEditor implements IValueEditor {

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
			result.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					okCancel.ok();
				}
			});
			result.setText(originalValue);
			result.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					updateEnabledStatusOfButtons();
				}
			});
			result.setFocus();
			result.selectAll();
			return result;
		}

		@Override
		protected boolean useAllHeight() {
			return false;
		}

		@Override
		protected String getValue() {
			return editorControl == null ? null : editorControl.getText();
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

	@Override
	public String getValue() {
		return content.editorControl.getText();
	}

	public static void main(String[] args) {
		Swts.displayNoLayout(TextEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				TextEditor textEditor = new TextEditor(from, cardConfig, "someUrl", "key", "value", IDetailsFactoryCallback.Utils.resizeAfterGotData(), TitleSpec.noTitleSpec(from.getBackground()));
				Swts.resizeMeToParentsSize(textEditor.getControl());
				textEditor.content.layout();
				Swts.layoutDump(from);
				Swts.resizeMeToParentsSizeWithLayout(textEditor);
				return textEditor.content;
			}
		});
	}

	@Override
	public String getTitleText() {
		return content.titleLabel.getText();
	}

	@Override
	public OkCancel getOkCancel() {
		return content.okCancel;
	}
	@Override
	public void setValue(String newValue) {
		 content.editorControl.setText(newValue);
	}

}
