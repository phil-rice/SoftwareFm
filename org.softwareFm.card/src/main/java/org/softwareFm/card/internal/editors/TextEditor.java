package org.softwareFm.card.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.IMutableCardDataStore;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;

public class TextEditor implements IHasControl {

	private final TextEditorComposite content;

	static class TextEditorComposite extends Composite {

		public Label titleLabel;
		private final Text text;
		public Button okButton;
		private final Button cancelButton;
		private final String originalValue;

		public TextEditorComposite(Composite parent, int style, final ICard card, final CardConfig cardConfig, final String key, Object value, final Runnable afterEdit) {
			super(parent, style);
			titleLabel = new Label(this, SWT.NULL);
			KeyValue keyValue = new KeyValue(key, value);
			String name = Functions.call(cardConfig.nameFn, keyValue);
			titleLabel.setText(name);

			text = new Text(this, SWT.WRAP);
			originalValue = Functions.call(cardConfig.valueFn, keyValue);
			text.setText(originalValue);
			okButton = new Button(this, SWT.PUSH);
			okButton.setText(IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, DisplayConstants.buttonOkTitle));
			cancelButton = new Button(this, SWT.PUSH);
			cancelButton.setText(IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, DisplayConstants.buttonCancelTitle));

			Swts.addGrabHorizontalAndFillGridDataToAllChildren(this);
			updateEnabledStatusOfButtons();
			text.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					updateEnabledStatusOfButtons();
				}
			});
			okButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					IMutableCardDataStore cardDataStore = (IMutableCardDataStore) cardConfig.cardDataStore;
					cardDataStore.put(card.url(), Maps.<String, Object> makeMap(key, text.getText()), afterEdit);
					TextEditorComposite.this.dispose();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			cancelButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					TextEditorComposite.this.dispose();
				}
			});
		}

		private void updateEnabledStatusOfButtons() {
			okButton.setEnabled(!originalValue.equals(text.getText()));
		}
	}

	public TextEditor(Composite parentComposite, ICard card, CardConfig cardConfig, String key, Object value, Runnable afterEdit) {
		content = new TextEditorComposite(parentComposite, SWT.NULL, card, cardConfig, key, value, afterEdit);
	}

	@Override
	public Control getControl() {
		return content;
	}

	public Label getTitleLabel() {
		return content.titleLabel;
	}

	public Text getText() {
		return content.text;
	}

	public Button getOkButton() {
		return content.okButton;
	}

	public Button getCancelButton() {
		return content.cancelButton;
	}

}
