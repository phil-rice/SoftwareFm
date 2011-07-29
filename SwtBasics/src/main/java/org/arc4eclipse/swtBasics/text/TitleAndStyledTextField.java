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
		fd_lblTitle.right = new FormAttachment(0, 185);

		txtText = new StyledText(this, SWT.BORDER);
		FormData fd_styledText = new FormData();
		fd_styledText.bottom = new FormAttachment(100, 0);
		fd_styledText.right = new FormAttachment(100, 0);
		fd_styledText.top = new FormAttachment(0, 30);
		fd_styledText.left = new FormAttachment(0, 0);
		txtText.setLayoutData(fd_styledText);

	}

	public void setText(final String text) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				txtText.setText(text);
			}
		});
	}

	public void appendText(final String text) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				txtText.append(text);
			}
		});
	}

	public String getText() {
		return txtText.getText();
	}
}
