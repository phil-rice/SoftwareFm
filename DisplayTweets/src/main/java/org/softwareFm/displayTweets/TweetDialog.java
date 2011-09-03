package org.softwareFm.displayTweets;

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

public class TweetDialog extends Dialog {
	protected String result;
	private final ConfigForTitleAnd config;
	private final String titleKey;

	public TweetDialog(Shell parent, int style, ConfigForTitleAnd config, String titleKey) {
		super(parent, style);
		this.config = config;
		this.titleKey = titleKey;
	}

	public String open(String initial) {
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

	private void createContents(final Shell shell, String initial) {
		final TitleAndTextField txt = new TitleAndTextField(config, shell, titleKey);
		txt.setEditable(true);
		txt.setText(initial);
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
				result = txt.getText();
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
		Swts.display(TweetDialog.class.getSimpleName(), new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(final Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				composite.setLayout(new GridLayout());
				Button button = new Button(composite, SWT.PUSH);
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						TweetDialog dialog = new TweetDialog(from.getShell(), SWT.NULL, ConfigForTitleAnd.createForBasics(from.getDisplay()), "Name");
						String open = dialog.open("initial");
						System.out.println("Result: " + open);
					}
				});
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}
		});
	}
}
