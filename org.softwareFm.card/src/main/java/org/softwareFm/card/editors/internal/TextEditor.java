package org.softwareFm.card.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.utilities.functions.IFunction1;

public class TextEditor implements IValueEditorForTests {

	private final TextEditorComposite content;

	public static class TextEditorComposite extends ValueEditorComposite<Text> {

		public TextEditorComposite(Composite parent, int style, final CardConfig cardConfig, final String url,String cardType, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
			super(parent, style, cardConfig, url, cardType, key, initialValue, titleSpec, callback);

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
		public boolean useAllHeight() {
			return false;
		}

		@Override
		protected String getValue() {
			return getEditor() == null ? null : getEditor().getText();
		}
	}

	public TextEditor(Composite parentComposite, CardConfig cardConfig, String url, String cardType,String key, Object value, IDetailsFactoryCallback callback, TitleSpec titleSpec) {
		content = new TextEditorComposite(parentComposite, SWT.NULL, cardConfig, url, cardType,key, value, titleSpec, callback);
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
		return content.getEditor().getText();
	}

	public static void main(String[] args) {
		Show.displayNoLayout(TextEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				TextEditor TextEditor = new TextEditor(from, cardConfig, "someUrl", null, "key", "value", IDetailsFactoryCallback.Utils.resizeAfterGotData(), TitleSpec.noTitleSpec(from.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN)));
				TextEditor.getComposite().setLayout(new ValueEditorLayout());
//				Size.resizeMeToParentsSize(textEditor.getControl());
//				textEditor.content.layout();
				Swts.layoutDump(from);
				Size.resizeMeToParentsSizeWithLayout(TextEditor);
				return TextEditor.content;
			}
		});
	}

	@Override
	public String getTitleText() {
		return content.title.getText();
	}

	@Override
	public OkCancel getOkCancel() {
		return content.getOkCancel();
	}
	@Override
	public void setValue(String newValue) {
		 content.getEditor().setText(newValue);
	}

}
