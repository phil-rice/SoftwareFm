package org.softwareFm.displayLists;

import org.eclipse.swt.SWT;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.TitleAndTextField;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.strings.NameAndValue;

public class NameAndValueDialog extends Dialog {

	protected NameAndValue result;
	private final ConfigForTitleAnd config;
	private final String nameTitle;
	private final String valueTitle;

	public NameAndValueDialog(Shell parent, int style, ConfigForTitleAnd config, String nameTitle, String valueTitle) {
		super(parent, style);
		this.config = config;
		this.nameTitle = nameTitle;
		this.valueTitle = valueTitle;
	}

	public NameAndValue open(NameAndValue initial) {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell, initial);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		return result;
	}

	private void createContents(final Shell shell, NameAndValue initial) {
		final TitleAndTextField txtName = new TitleAndTextField(config, shell, nameTitle, false);
		txtName.setEditable(true);
		final TitleAndTextField txtUrl = new TitleAndTextField(config, shell, valueTitle, false);
		txtUrl.setEditable(true);
		txtName.setText(initial.name);
		txtUrl.setText(initial.value);
		Composite buttonComposite = new Composite(shell, SWT.NULL);
		buttonComposite.setLayout(new GridLayout(2, false));
		// Create the cancel button and addKey a handler
		// so that pressing it will set input to null
		Button cancel = new Button(buttonComposite, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				result = null;
				shell.close();
			}
		});
		Button ok = new Button(buttonComposite, SWT.PUSH);
		ok.setText("OK");
		ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				result = new NameAndValue(txtName.getText(), txtUrl.getText());
				shell.close();
			}
		});

		// Set the OK button as the default, so
		// user can type input and press Enter
		// to dismiss
		shell.setDefaultButton(ok);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(shell);
	}

	public static void main(String[] args) {
		Swts.display(NameAndValueDialog.class.getSimpleName(), new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(final Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				composite.setLayout(new GridLayout());
				Button button = new Button(composite, SWT.PUSH);
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						NameAndValueDialog nameAndValueDialog = new NameAndValueDialog(from.getShell(), SWT.NULL, ConfigForTitleAnd.createForBasics(from.getDisplay()), "Name", "Value");
						NameAndValue open = nameAndValueDialog.open(new NameAndValue("name", "url"));
						System.out.println("Result: " + open);
					}
				});
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}
		});
	}
}
