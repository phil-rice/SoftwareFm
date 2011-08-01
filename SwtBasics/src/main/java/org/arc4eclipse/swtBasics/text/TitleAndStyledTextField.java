package org.arc4eclipse.swtBasics.text;

import org.arc4eclipse.swtBasics.images.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

public class TitleAndStyledTextField extends AbstractTitleAnd {

	private final StyledText txtText;

	public TitleAndStyledTextField(Composite arg0, int arg1, Images images, String title) {
		super(arg0, arg1, images, title);
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
