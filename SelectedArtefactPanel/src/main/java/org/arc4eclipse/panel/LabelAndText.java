package org.arc4eclipse.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LabelAndText extends Composite {
	private final Text text;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public LabelAndText(Composite parent, int style, String title) {
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

		text = new Text(this, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(btnNewButton, -6);
		fd_text.top = new FormAttachment(lblNewLabel, 0, SWT.TOP);
		fd_text.left = new FormAttachment(0, 82);
		text.setLayoutData(fd_text);

	}

	public void setText(String text) {
		this.text.setText(text);
	}

	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public String getText() {
		return this.text.getText();
	}

	public void addModifyListener(ModifyListener listener) {
		text.addModifyListener(listener);

	}

}
