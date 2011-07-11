package org.arc4eclipse.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class LabelAndList extends Composite {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public LabelAndList(Composite parent, int style, String title) {
		super(parent, style);
		setLayout(new FormLayout());

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new FormData());
		label.setText(title == null ? "" : title);

		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.bottom = new FormAttachment(100);
		fd_scrolledComposite.left = new FormAttachment(0);
		fd_scrolledComposite.top = new FormAttachment(0, 21);
		fd_scrolledComposite.right = new FormAttachment(100);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		List list = new List(scrolledComposite, SWT.BORDER);
		scrolledComposite.setContent(list);
		scrolledComposite.setMinSize(list.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Button btnNewButton = new Button(this, SWT.NONE);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.top = new FormAttachment(label, 0, SWT.TOP);
		fd_btnNewButton.right = new FormAttachment(100);
		fd_btnNewButton.left = new FormAttachment(100, -28);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("Add");

	}

	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
