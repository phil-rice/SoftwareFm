package org.softwareFm.displayJavadocAndSource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.swtBasics.Swts;
import org.softwareFm.swtBasics.images.Resources;
import org.softwareFm.swtBasics.text.ConfigForTitleAnd;
import org.softwareFm.swtBasics.text.TitleAndTextField;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.strings.Strings;

public class ReconciliationDialog extends Dialog {

	private final ConfigForTitleAnd config;
	private final String key;
	private final ISendToEclipseOrRepository sendToEclipseOrRepository;

	public ReconciliationDialog(Shell parent, ISendToEclipseOrRepository sendToEclipseOrRepository, ConfigForTitleAnd config, String key) {
		super(parent);
		this.sendToEclipseOrRepository = sendToEclipseOrRepository;
		this.config = config;
		this.key = key;
	}

	public void open(String eclipseValue, String repositoryValue) {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell, eclipseValue, repositoryValue);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents(final Shell shell, final String eclipseValue, final String repositoryValue) {
		shell.setLayout(new GridLayout());
		TitleAndTextField txtSoftwareFm = new TitleAndTextField(config, shell, key + ".repository");
		TitleAndTextField txtEclipse = new TitleAndTextField(config, shell, key + ".eclipse");

		txtSoftwareFm.setText(Strings.nullSafeToString(repositoryValue));
		txtEclipse.setText(Strings.nullSafeToString(eclipseValue));
		Composite buttons = new Composite(shell, SWT.NULL);
		buttons.setLayout(new RowLayout());

		Button copyToRepositoryButton = makeButton(buttons, JavadocSourceConstants.copyToRepositoryKey, eclipseValue != null);
		Button copyToEclipseButton = makeButton(buttons, JavadocSourceConstants.copyToEclipseKey, repositoryValue != null);
		Button clearEclipseButton = makeButton(buttons, JavadocSourceConstants.clearEclipseKey, repositoryValue != null);
		Button cancelButton = makeButton(buttons, JavadocSourceConstants.reconciliationCancelKey, true);
		Swts.<Button> setRowDataFor(100, config.buttonHeight, copyToRepositoryButton, copyToEclipseButton, clearEclipseButton);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(shell);

		copyToRepositoryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					sendToEclipseOrRepository.sendToRepository(eclipseValue);
				} catch (Exception e1) {
					throw WrappedException.wrap(e1);
				}
				shell.close();
			}
		});
		copyToEclipseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendToEclipseOrRepository.sendToEclipse(repositoryValue);
				shell.close();
			}
		});
		clearEclipseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendToEclipseOrRepository.clearEclipseValue();
				shell.close();
			}
		});
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
	}

	private Button makeButton(Composite buttons, String buttonKey, boolean enabled) {
		Button copyToRepositoryButton = new Button(buttons, SWT.NULL);
		copyToRepositoryButton.setText(Resources.getOrException(config.resourceGetter, buttonKey));
		copyToRepositoryButton.setEnabled(enabled);
		return copyToRepositoryButton;
	}
}
