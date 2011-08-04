package org.arc4eclipse.displayLists;

import org.arc4eclipse.swtBasics.Swts;
import org.arc4eclipse.swtBasics.images.IImageButtonListener;
import org.arc4eclipse.swtBasics.images.IImageFactory;
import org.arc4eclipse.swtBasics.images.ImageButton;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.arc4eclipse.utilities.functions.IFunction1;
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

public class NameAndUrlDialog extends Dialog {

	private final IImageFactory imageFactory;
	protected NameAndUrl result;

	public NameAndUrlDialog(Shell parent, IImageFactory imageFactory) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, imageFactory);
	}

	public NameAndUrlDialog(Shell parent, int style, IImageFactory imageFactory) {
		super(parent, style);
		this.imageFactory = imageFactory;
	}

	public NameAndUrl open(NameAndUrl initial) {
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

	private void createContents(final Shell shell, NameAndUrl initial) {
		final TitleAndTextField txtName = new TitleAndTextField(shell, imageFactory, "Name", false);
		txtName.setEditable(true);
		final TitleAndTextField txtUrl = new TitleAndTextField(shell, imageFactory, "Url", false);
		txtUrl.setEditable(true);
		txtName.setText(initial.name);
		txtUrl.setText(initial.url);
		Composite buttonComposite = new Composite(shell, SWT.NULL);
		buttonComposite.setLayout(new GridLayout(2, false));
		// Create the cancel button and add a handler
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
				result = new NameAndUrl(txtName.getText(), txtUrl.getText());
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
		final IImageFactory imageFactory = IImageFactory.Utils.imageFactory();
		Swts.display("", new IFunction1<Composite, Composite>() {

			@Override
			public Composite apply(final Composite from) throws Exception {
				Composite composite = new Composite(from, SWT.NULL);
				ImageButton imageButton = new ImageButton(composite, imageFactory.makeImages(from.getDisplay()).getEditImage());
				imageButton.addListener(new IImageButtonListener() {
					@Override
					public void buttonPressed(ImageButton button) {
						NameAndUrlDialog nameAndUrlDialog = new NameAndUrlDialog(from.getShell(), imageFactory);
						NameAndUrl open = nameAndUrlDialog.open(new NameAndUrl("name", "url"));
						System.out.println("Result: " + open);
					}
				});
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;
			}
		});
	}
}
