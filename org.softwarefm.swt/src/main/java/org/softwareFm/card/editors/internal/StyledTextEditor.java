/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.editors.IValueEditor;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;

public class StyledTextEditor implements IValueEditor {

	private final TextEditorComposite content;

	static class TextEditorComposite extends ValueEditorComposite<StyledText> {

		public TextEditorComposite(Composite parent, int style, final CardConfig cardConfig, final String url, String cardType, final String key, Object initialValue, TitleSpec titleSpec, final IDetailsFactoryCallback callback) {
			super(parent, style, cardConfig, url, cardType, key, initialValue, titleSpec, callback);
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
			return getEditor().getText();
		}
	}

	public StyledTextEditor(Composite parentComposite, CardConfig cardConfig, String url, String cardType, String key, Object value, IDetailsFactoryCallback callback, TitleSpec titleSpec) {
		content = new TextEditorComposite(parentComposite, SWT.NULL, cardConfig, url, cardType, key, value, titleSpec, callback);
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
		return content.getEditor();
	}

	public TitleSpec getTitleSpec() {
		return content.getTitleSpec();
	}

	public static void main(String[] args) {
		Show.displayNoLayout(StyledTextEditor.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				StyledTextEditor textEditor = new StyledTextEditor(from, cardConfig, "someUrl", null, "key", "value", IDetailsFactoryCallback.Utils.resizeAfterGotData(), TitleSpec.noTitleSpec(from.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN)));
				textEditor.getComposite().setLayout(new ValueEditorLayout());
				Size.resizeMeToParentsSizeWithLayout(textEditor);
				return textEditor.content;
			}
		});
	}

}