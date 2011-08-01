package org.arc4eclipse.swtBasics.text;

import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.images.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class TitleAndTextField extends AbstractTitleAnd {
	private final Text txtText;
	private final Color originalBackground;

	public TitleAndTextField(Composite parent, Images images, String title, boolean editable) {
		this(parent, SWT.BORDER, images, title, editable);
	}

	public TitleAndTextField(Composite parent, int style, Images images, String title, boolean editable) {
		super(parent, style, images, title);

		txtText = new Text(this, SWT.BORDER);
		FormData fd_txtValue = new FormData();
		fd_txtValue.top = new FormAttachment(0, 20);
		fd_txtValue.left = new FormAttachment(0);
		fd_txtValue.bottom = new FormAttachment(100, 0);
		fd_txtValue.right = new FormAttachment(100, 0);
		txtText.setLayoutData(fd_txtValue);
		txtText.setText("");
		txtText.setEditable(false);

		originalBackground = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		if (editable)
			addEditButton(images);
		updateBackground();
	}

	private void addEditButton(Images images) {
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

	public void setText(String text) {
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
