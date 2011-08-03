package org.arc4eclipse.swtBasics.text;

import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class TitleAndTextField extends AbstractTitleAnd {
	private final Text txtText;
	private final Color originalBackground;

	public TitleAndTextField(Composite parent, IImageFactory imageFactory, String title, boolean editable) {
		this(parent, SWT.BORDER, imageFactory, title, editable);
	}

	public TitleAndTextField(Composite parent, int style, IImageFactory imageFactory, String title, boolean editable) {
		super(parent, style, imageFactory, title);

		txtText = new Text(this, SWT.BORDER);
		txtText.setText("");
		txtText.setEditable(false);

		originalBackground = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		if (editable)
			addEditButton();
		updateBackground();
		RowData rowData = new RowData(400, 12);
		txtText.setLayoutData(rowData);
		txtText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				txtText.setToolTipText(txtText.getText());
			}
		});
	}

	private void addEditButton() {
		addCrListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				txtText.setEditable(false);
				updateBackground();
			}
		});
		addButton(images.getEditImage(), "Toggles the editable state of this value", new IImageButtonListener() {
			@Override
			public void buttonPressed(ImageButton button) {
				txtText.setEditable(!txtText.getEditable());
				updateBackground();
			}
		});
	}

	protected void updateBackground() {
		if (txtText.getEditable())
			txtText.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
		else
			txtText.setBackground(originalBackground);

	}

	public void setText(String rawText) {
		String text = Strings.nullSafeToString(rawText);
		txtText.setToolTipText(text);
		txtText.setText(text);
	}

	public String getText() {
		return txtText.getText();
	}

	public int getAsInteger() {
		return Integer.parseInt(getText());
	}

	public void addModifyListener(ModifyListener listener) {
		txtText.addModifyListener(listener);
	}

	@Override
	public String toString() {
		return "TitleAndTextField [title=" + getTitle() + ", text =" + txtText.getText() + "]";
	}

	public void addCrListener(Listener listener) {
		txtText.addListener(SWT.DefaultSelection, listener);
	}
}
