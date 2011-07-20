package org.arc4eclipse.swtBasics.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TitleAndStyledTextField extends Composite {

	private final StyledText txtText;

	public TitleAndStyledTextField(Composite arg0, int arg1, String title) {
		super(arg0, arg1);
		setLayout(new FormLayout());

		Label lblTitle = new Label(this, SWT.NONE);
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.top = new FormAttachment(0);
		fd_lblTitle.left = new FormAttachment(0);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText(title == null ? "" : title);

		txtText = new StyledText(this, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		fd_lblTitle.right = new FormAttachment(txtText, -6);
		FormData fd_txtText = new FormData();
		fd_txtText.bottom = new FormAttachment(100, 0);
		fd_txtText.left = new FormAttachment(0, 75);
		fd_txtText.right = new FormAttachment(100, 0);
		fd_txtText.top = new FormAttachment(0, 0);
		txtText.setLayoutData(fd_txtText);

	}

	public void setText(String text) {
		txtText.setText(text);
	}

	public void appendText(String text) {
		txtText.append(text);
	}

	public String getText() {
		return txtText.getText();
	}
}
