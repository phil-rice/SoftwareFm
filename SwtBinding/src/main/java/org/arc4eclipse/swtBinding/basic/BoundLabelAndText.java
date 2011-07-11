package org.arc4eclipse.swtBinding.basic;

import java.text.MessageFormat;
import java.util.Arrays;

import org.arc4eclipse.repositoryFacard.IRepositoryFacardCallback;
import org.arc4eclipse.swtBinding.constants.SwtBindingMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BoundLabelAndText<Key, Thing, Aspect, Data> extends BoundCompositeWithValue<Key, Thing, Aspect, Data> {
	private final Text text;
	private boolean updating;
	protected String startText;

	/**
	 * Create the composite.
	 * 
	 * @param style
	 * @param factory
	 * @param aspect
	 * @param keys
	 */
	public BoundLabelAndText(Composite parent, int style, String title, Aspect aspect, final String... keys) {
		super(parent, style, aspect, keys);
		setLayout(new FormLayout());

		Label lblNewLabel = new Label(this, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0);
		fd_lblNewLabel.left = new FormAttachment(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText(title == null ? "" : title);

		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent arg0) {
				startText = text.getText();
				text.setEditable(!text.getEditable());
			}
		});
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.top = new FormAttachment(0);
		fd_btnNewButton.right = new FormAttachment(100);
		fd_btnNewButton.left = new FormAttachment(100, -22);
		btnNewButton.setLayoutData(fd_btnNewButton);
		btnNewButton.setText("...");

		text = new Text(this, SWT.BORDER);
		text.setEditable(false);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(btnNewButton, -6);
		fd_text.top = new FormAttachment(lblNewLabel, 0, SWT.TOP);
		fd_text.left = new FormAttachment(0, 82);
		text.setLayoutData(fd_text);
		text.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent arg0) {
				if (!updating && text.getEditable() && !startText.equals(text.getText())) {
					logger.debug(MessageFormat.format(SwtBindingMessages.changing, Arrays.asList(keys), text.getText()));
					updateDataWith(text.getText(), new IRepositoryFacardCallback<Key, Thing, Aspect, Data>() {
						public void process(Key key, Thing thing, Aspect aspect, Data data) {
							logger.debug(MessageFormat.format(SwtBindingMessages.changed, Arrays.asList(keys), text.getText()));
						}
					});
				}
			}

			public void focusGained(FocusEvent arg0) {
			}
		});

	}

	public void setText(String text) {
		assert !updating;
		updating = true;
		try {
			this.text.setText(text);
		} finally {
			updating = false;
		}
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

	
	protected void setFromValue(Key key, Thing thing, Data data, Object value) {
		logger.debug(MessageFormat.format(SwtBindingMessages.setFromValue, key, Arrays.asList(keys), data, value));
		String string = value == null ? "" : value.toString();
		setText(string);
	}
}
