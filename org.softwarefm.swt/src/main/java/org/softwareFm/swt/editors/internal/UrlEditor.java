/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors.internal;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.editors.internal.TextEditor.TextEditorComposite;
import org.softwareFm.swt.okCancel.IOkCancel;
import org.softwareFm.swt.title.TitleSpec;

public class UrlEditor implements IValueEditorForTests {

	public final UrlEditorComposite content;

	static class UrlEditorComposite extends TextEditorComposite {

		private Button testButton;

		public UrlEditorComposite(final Composite parent, int style, final CardConfig cardConfig, final String url, String cardType, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
			super(parent, style, cardConfig, url, cardType, key, initialValue, titleSpec, callback);
			updateEnabledStatusOfButtons();
		}

		@Override
		protected void addAnyMoreButtons() {
			testButton = getFooter().addButton(CardConstants.buttonTestTitle, new Runnable() {
				@Override
				public void run() {
					try {
						Desktop.getDesktop().browse(new URI(getValue()));
					} catch (Exception e) {
						MessageDialog.openError(getShell(), "Error", "Illegal Url");
					}
				}
			});
			testButton.moveAbove(getFooter().okButton());
		}

		@SuppressWarnings("unused")
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
			getFooter().setOkEnabled(enable);
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


	@Override
	public String getTitleText() {
		return content.getTitle().getText();
	}

	@Override
	public IOkCancel getOkCancel() {
		return content.getFooter();
	}

	@Override
	public void setValue(String newValue) {
		content.getEditor().setText(newValue);
	}

}