package org.arc4eclipse.swtBasics.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TitleAndTextField extends Composite {
	private final Text txtText;

	public TitleAndTextField(Composite arg0, int arg1, String title) {
		super(arg0, arg1);
		setLayout(new FormLayout());

		Label lblTitle = new Label(this, SWT.NONE);
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.top = new FormAttachment(0);
		fd_lblTitle.left = new FormAttachment(0);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText(title == null ? "" : title);

		txtText = new Text(this, SWT.BORDER);
		fd_lblTitle.bottom = new FormAttachment(0, 25);
		fd_lblTitle.right = new FormAttachment(0, 240);
		FormData fd_txtValue = new FormData();
		fd_txtValue.top = new FormAttachment(0, 31);
		fd_txtValue.left = new FormAttachment(0);
		fd_txtValue.bottom = new FormAttachment(100, 0);
		fd_txtValue.right = new FormAttachment(100, 0);
		txtText.setLayoutData(fd_txtValue);
		txtText.setText("");
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
}
