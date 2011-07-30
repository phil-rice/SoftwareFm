package org.arc4eclipse.swtBasics.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class TitleAndTextField extends Composite {
	private final Text txtText;
	private final Label lblTitle;
	private final Composite compForTitle;
	private final Color originalBackground;

	/**
	 * @wbp.parser.constructor
	 */
	public TitleAndTextField(Composite arg0, int arg1, String title) {
		this(arg0, arg1, title, true);
	}

	public TitleAndTextField(Composite arg0, int arg1, String title, boolean editable) {
		super(arg0, arg1);
		setLayout(new FormLayout());

		compForTitle = new Composite(this, SWT.NULL);
		FormData fd_compButtons = new FormData();
		fd_compButtons.left = new FormAttachment(0, 0);
		fd_compButtons.right = new FormAttachment(100, 0);
		compForTitle.setLayoutData(fd_compButtons);
		RowLayout layout = new RowLayout();
		compForTitle.setLayout(layout);
		layout.marginTop = 0;
		layout.marginBottom = 0;

		lblTitle = new Label(compForTitle, SWT.NONE);
		lblTitle.setText(title == null ? "" : title);

		txtText = new Text(this, SWT.BORDER);
		FormData fd_txtValue = new FormData();
		fd_txtValue.top = new FormAttachment(0, 20);
		fd_txtValue.left = new FormAttachment(0);
		fd_txtValue.bottom = new FormAttachment(100, 0);
		fd_txtValue.right = new FormAttachment(100, 0);
		txtText.setLayoutData(fd_txtValue);
		txtText.setText("");
		txtText.setEditable(false);

		originalBackground = txtText.getBackground();
		addEditButton(editable);
	}

	public Button addButton(String title, SelectionListener listener) {
		Button button = new Button(compForTitle, SWT.PUSH);
		button.setText(title);
		// button.setSize(new Point(35, 12));
		button.addSelectionListener(listener);
		RowData data = new RowData();
		data.height = 18;
		button.setLayoutData(data);
		return button;
	}

	private void addEditButton(boolean editable) {
		addCrListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				txtText.setEditable(false);
				txtText.setBackground(originalBackground);
			}
		});
		addButton("Edit", new SelectionListener() {
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
		// Button btnEdit = new Button(this, SWT.NONE);
		// FormData fd_btnEdi = new FormData();
		// fd_btnEdi.bottom = new FormAttachment(txtText);
		// fd_btnEdi.right = new FormAttachment(100);
		// fd_btnEdi.top = new FormAttachment(0);
		// fd_btnEdi.left = new FormAttachment(100, -35);
		// btnEdit.setLayoutData(fd_btnEdi);
		// btnEdit.setText("Edit");
		// btnEdit.setEnabled(editable);
		// btnEdit.addSelectionListener();
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
