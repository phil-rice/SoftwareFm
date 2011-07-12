package org.arc4eclipse.MasterDetailPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

public class MasterDetailPanel<T> extends Composite implements IMasterDetailPanel<T> {

	private final TableWithTitle tableWithTitle;
	private final BrowserWithTitle browserWithTitle;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */

	public MasterDetailPanel(Composite parent, int style, String masterTitle, String detailTitle) {
		super(parent, style);
		setLayout(new FormLayout());

		tableWithTitle = new TableWithTitle(this, SWT.NONE, masterTitle);
		FormData fd_tableWithTitle = new FormData();
		fd_tableWithTitle.bottom = new FormAttachment(0, 290);
		fd_tableWithTitle.right = new FormAttachment(0, 177);
		fd_tableWithTitle.top = new FormAttachment(0);
		fd_tableWithTitle.left = new FormAttachment(0);
		tableWithTitle.setLayoutData(fd_tableWithTitle);

		browserWithTitle = new BrowserWithTitle(this, SWT.NONE, detailTitle);
		FormData fd_browserWithTitle = new FormData();
		fd_browserWithTitle.left = new FormAttachment(tableWithTitle, 0);
		fd_browserWithTitle.top = new FormAttachment(tableWithTitle, 0, SWT.TOP);
		fd_browserWithTitle.bottom = new FormAttachment(100, 0);
		fd_browserWithTitle.right = new FormAttachment(100, 0);
		browserWithTitle.setLayoutData(fd_browserWithTitle);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void clear() {
	}

	@Override
	public void append(T data) {
	}

	@Override
	public void setDetailHtmlGetter(IHtmlDetailPopulator<T> populator) {
	}
}
