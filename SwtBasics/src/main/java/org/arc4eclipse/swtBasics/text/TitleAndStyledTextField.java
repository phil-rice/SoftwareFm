package org.arc4eclipse.swtBasics.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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

		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.bottom = new FormAttachment(100, 0);
		fd_scrolledComposite.right = new FormAttachment(100, 0);
		fd_scrolledComposite.top = new FormAttachment(0, 25);
		fd_scrolledComposite.left = new FormAttachment(0, 0);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		txtText = new StyledText(scrolledComposite, SWT.BORDER | SWT.WRAP);
		scrolledComposite.setContent(txtText);
		scrolledComposite.setMinSize(txtText.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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
