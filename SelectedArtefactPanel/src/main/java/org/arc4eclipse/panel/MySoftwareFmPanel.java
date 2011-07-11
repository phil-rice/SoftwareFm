package org.arc4eclipse.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

public class MySoftwareFmPanel extends Composite {
	private final Table table;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MySoftwareFmPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		Label lblNewLabel = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 37);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Status");

		Combo combo = new Combo(this, SWT.NONE);
		FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(0, 356);
		fd_combo.top = new FormAttachment(0, 37);
		fd_combo.left = new FormAttachment(0, 83);
		combo.setLayoutData(fd_combo);

		Label lblFriends = new Label(this, SWT.NONE);
		FormData fd_lblFriends = new FormData();
		fd_lblFriends.top = new FormAttachment(0, 70);
		fd_lblFriends.left = new FormAttachment(0, 10);
		lblFriends.setLayoutData(fd_lblFriends);
		lblFriends.setText("Friends");

		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.bottom = new FormAttachment(100);
		fd_scrolledComposite.right = new FormAttachment(0, 345);
		fd_scrolledComposite.top = new FormAttachment(0, 71);
		fd_scrolledComposite.left = new FormAttachment(0, 83);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		table = new Table(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		scrolledComposite.setContent(table);
		scrolledComposite.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		LabelAndText labelAndText = new LabelAndText(this, SWT.NONE, "My Email");
		FormData fd_labelAndText = new FormData();
		fd_labelAndText.right = new FormAttachment(0, 335);
		fd_labelAndText.top = new FormAttachment(combo, -27, SWT.TOP);
		fd_labelAndText.bottom = new FormAttachment(combo, -6);
		fd_labelAndText.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		labelAndText.setLayoutData(fd_labelAndText);

		LabelAndList commentsList = new LabelAndList(this, SWT.NONE, "My Comments");
		FormData fd_labelAndList = new FormData();
		fd_labelAndList.bottom = new FormAttachment(100);
		fd_labelAndList.right = new FormAttachment(100, -135);
		fd_labelAndList.top = new FormAttachment(0, 60);
		fd_labelAndList.left = new FormAttachment(0, 490);
		commentsList.setLayoutData(fd_labelAndList);

	}

	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
