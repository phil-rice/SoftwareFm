package org.arc4eclipse.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LabelAndStyledText extends Composite {

	private StyledText styledText;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public LabelAndStyledText(Composite parent, int style, String title) {
		super(parent, style);
		setLayout(new FormLayout());

		Label lblNewLabel = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0);
		fd_lblNewLabel.left = new FormAttachment(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText(title == null ? "" : title);

		Button btnNewButton = new Button(this, SWT.NONE);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.top = new FormAttachment(0);
		fd_btnNewButton.right = new FormAttachment(100);
		fd_btnNewButton.left = new FormAttachment(100, -22);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("...");
		fd_btnNewButton.bottom = new FormAttachment(0, 19);

		styledText = new StyledText(this, SWT.BORDER);
		FormData fd_styledText = new FormData();
		fd_styledText.top = new FormAttachment(0, 21);
		fd_styledText.left = new FormAttachment(0);
		fd_styledText.bottom = new FormAttachment(100);
		fd_styledText.right = new FormAttachment(100);
		styledText.setLayoutData(fd_styledText);

	}

	public void setText(String text) {
		this.styledText.setText(text);
	}

	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
