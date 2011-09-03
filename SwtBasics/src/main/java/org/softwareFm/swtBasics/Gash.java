package org.softwareFm.swtBasics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class Gash extends Composite {

	public Gash(Composite parent, int style) {
		super(parent, style);
		setLayout(new RowLayout(SWT.HORIZONTAL));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FormLayout());

		Composite composite_1 = new Composite(composite, SWT.NONE);
		FormData fd_composite_1 = new FormData();
		fd_composite_1.bottom = new FormAttachment(0, 64);
		fd_composite_1.right = new FormAttachment(0, 234);
		fd_composite_1.top = new FormAttachment(0);
		fd_composite_1.left = new FormAttachment(0, 111);
		composite_1.setLayoutData(fd_composite_1);
		composite_1.setLayout(new FormLayout());

		Button button = new Button(composite_1, SWT.NONE);
		FormData fd_button = new FormData();
		fd_button.right = new FormAttachment(100, -12);
		fd_button.left = new FormAttachment(100, -37);
		button.setLayoutData(fd_button);
		button.setText("B1");

		Button btnB = new Button(composite_1, SWT.NONE);
		fd_button.bottom = new FormAttachment(0, 35);
		fd_button.top = new FormAttachment(0, 10);
		FormData fd_btnB = new FormData();
		fd_btnB.bottom = new FormAttachment(button, 25);
		fd_btnB.top = new FormAttachment(button, 0, SWT.TOP);
		fd_btnB.right = new FormAttachment(0, 60);
		fd_btnB.left = new FormAttachment(0, 35);
		btnB.setLayoutData(fd_btnB);
		btnB.setText("B2");

		Label lblLabel = new Label(composite, SWT.NONE);
		FormData fd_lblLabel = new FormData();
		fd_lblLabel.right = new FormAttachment(0, 65);
		fd_lblLabel.top = new FormAttachment(0, 10);
		fd_lblLabel.left = new FormAttachment(0, 10);
		lblLabel.setLayoutData(fd_lblLabel);
		lblLabel.setText("Label");
	}

	public static void main(String[] args) {

	}
}
