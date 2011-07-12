package org.arc4eclipse.MasterDetailPanel;

import org.arc4eclipse.swtBinding.constants.SwtBindingConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class TableWithTitle<T> extends Composite implements ITableData<T> {
	private final Table table;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public TableWithTitle(Composite parent, int style, String title) {
		super(parent, style);
		setLayout(new FormLayout());

		Label lblTitle = new Label(this, SWT.NONE);
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.bottom = new FormAttachment(0, SwtBindingConstants.titleHeight);
		fd_lblTitle.right = new FormAttachment(100);
		fd_lblTitle.top = new FormAttachment(0);
		fd_lblTitle.left = new FormAttachment(0);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText(title == null ? "" : title);

		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		FormData fd_scrolledComposite = new FormData();
		fd_scrolledComposite.top = new FormAttachment(0, SwtBindingConstants.topAftertitle);
		fd_scrolledComposite.bottom = new FormAttachment(100, 0);
		fd_scrolledComposite.right = new FormAttachment(100, 0);
		fd_scrolledComposite.left = new FormAttachment(0, 0);
		scrolledComposite.setLayoutData(fd_scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		table = new Table(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		scrolledComposite.setContent(table);
		scrolledComposite.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void clear() {
		table.removeAll();
	}

	@Override
	public void append(T data) {
		new TableItem(table, SWT.NULL).setData(data);
	}
}
