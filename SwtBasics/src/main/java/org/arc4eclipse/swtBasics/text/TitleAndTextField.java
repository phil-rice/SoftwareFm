package org.arc4eclipse.swtBasics.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class TitleAndTextField extends Composite {
	private final Text txtText;
	private final Label lblTitle;

	/**
	 * @wbp.parser.constructor
	 */
	public TitleAndTextField(Composite arg0, int arg1, String title) {
		this(arg0, arg1, title, true);
	}

	public TitleAndTextField(Composite arg0, int arg1, String title, boolean editable) {
		super(arg0, arg1);
		setLayout(new FormLayout());

		lblTitle = new Label(this, SWT.NONE);
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.top = new FormAttachment(0);
		fd_lblTitle.left = new FormAttachment(0);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText(title == null ? "" : title);

		txtText = new Text(this, SWT.BORDER);
		fd_lblTitle.bottom = new FormAttachment(0, 18);
		fd_lblTitle.right = new FormAttachment(0, 240);
		FormData fd_txtValue = new FormData();
		fd_txtValue.top = new FormAttachment(0, 20);
		fd_txtValue.left = new FormAttachment(0);
		fd_txtValue.bottom = new FormAttachment(100, 0);
		fd_txtValue.right = new FormAttachment(100, 0);
		txtText.setLayoutData(fd_txtValue);
		txtText.setText("");
		txtText.setEditable(false);
		final Color originalBackground = txtText.getBackground();
		addCrListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				txtText.setEditable(false);
				txtText.setBackground(originalBackground);
			}
		});
		Button btnEdit = new Button(this, SWT.NONE);
		FormData fd_btnEdi = new FormData();
		fd_btnEdi.bottom = new FormAttachment(txtText);
		fd_btnEdi.right = new FormAttachment(100);
		fd_btnEdi.top = new FormAttachment(0);
		fd_btnEdi.left = new FormAttachment(100, -35);
		btnEdit.setLayoutData(fd_btnEdi);
		btnEdit.setText("Edit");
		btnEdit.setEnabled(editable);
		btnEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				txtText.setEditable(true);
				txtText.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
			}
		});
	}

	public void setText(String text) {
		txtText.setText(text);
	}

	public String getText() {
		return txtText.getText();
	}

	public int getAsInteger() {
		return Integer.parseInt(getText());
	}

	public void addModifyListener(ModifyListener listener) {
		txtText.addModifyListener(listener);
	}

	@Override
	public String toString() {
		return "TitleAndTextField [title=" + lblTitle.getText() + ", text =" + txtText.getText() + "]";
	}

	public void addCrListener(Listener listener) {
		txtText.addListener(SWT.DefaultSelection, listener);
	}
}
