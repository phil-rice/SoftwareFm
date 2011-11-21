package org.softwareFm.card.editors.internal;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.okCancel.OkCancel;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.utilities.functions.IFunction1;

public class UrlEditor implements IValueEditorForTests {

	private final UrlEditorComposite content;

	static class UrlEditorComposite extends org.softwareFm.card.editors.internal.TextEditor.TextEditorComposite {

		private Button testButton;

		public UrlEditorComposite(final Composite parent, int style, final CardConfig cardConfig, final String url, String cardType, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
			super(parent, style, cardConfig, url, cardType, key, initialValue, titleSpec, callback);
			updateEnabledStatusOfButtons();
		}

		@Override
		protected void addAnyMoreButtons() {
			testButton = okCancel.addButton(CardConstants.testButtonKey, new Runnable() {
				@Override
				public void run() {
					try {
						Desktop.getDesktop().browse(new URI(getValue()));
					} catch (Exception e) {
						MessageDialog.openError(getShell(), "Error", "Illegal Url");
					}
				}
			});
			testButton.moveAbove(okCancel.okButton);
		}

		@Override
		protected void updateEnabledStatusOfButtons() {
			try {
				String text = getValue();
				new URI(text);
				enableButtons(!originalValue.equals(getValue()));
			} catch (URISyntaxException e) {
				enableButtons(false);
			}
		}

		private void enableButtons(boolean enable) {
			okCancel.setOkEnabled(enable);
			testButton.setEnabled(enable);
		}
	}

	public UrlEditor(Composite parentComposite, CardConfig cardConfig, String url, String cardType, String key, Object value, IDetailsFactoryCallback callback, TitleSpec titleSpec) {
		content = new UrlEditorComposite(parentComposite, SWT.NULL, cardConfig, url, cardType, key, value, titleSpec, callback);
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
		Show.displayNoLayout(UrlEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				UrlEditor textEditor = new UrlEditor(from, cardConfig, "someUrl", null, "key", "value", IDetailsFactoryCallback.Utils.resizeAfterGotData(), TitleSpec.noTitleSpec(from.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN)));
				textEditor.getComposite().setLayout(new ValueEditorLayout());
				// Size.resizeMeToParentsSize(textEditor.getControl());
				// textEditor.content.layout();
				Swts.layoutDump(from);
				Size.resizeMeToParentsSizeWithLayout(textEditor);
				return textEditor.content;
			}
		});
	}

	@Override
	public String getTitleText() {
		return content.title.getText();
	}

	@Override
	public OkCancel getOkCancel() {
		return content.okCancel;
	}

	@Override
	public void setValue(String newValue) {
		content.getEditor().setText(newValue);
	}

}
