package org.softwareFm.configuration.actions;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.Swts;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class JavadocOrSourceDialog extends Dialog {

	private Shell shell;
	private final String artefact;
	private TitleAndText txtEclipse;
	private TitleAndText txtSoftwareFm;

	public JavadocOrSourceDialog(Shell parent, String artefact) {
		super(parent);
		this.artefact = artefact;
	}

	public void open(CompositeConfig compositeConfig, IDataGetter dataGetter) {
		shell = new Shell(getParentShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		createContents(shell, compositeConfig, dataGetter);
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(shell);
		shell.pack();
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@Override
	public Shell getShell() {
		return shell;
	}

	private void createContents(Shell parent, CompositeConfig compositeConfig, IDataGetter dataGetter) {
		IResourceGetter resourceGetter = compositeConfig.resourceGetter;
		String eclipseValue = Strings.nullSafeToString(dataGetter.getDataFor("data.raw." + artefact));
		String softwareFmValue = Strings.nullSafeToString(dataGetter.getDataFor("data.jar." + artefact));
		
		txtEclipse = new TitleAndText(compositeConfig, parent, "dialog.eclipseValue.title", true);
		txtSoftwareFm = new TitleAndText(compositeConfig, parent, "dialog.softwareFmValue.title", true);

		Composite buttonPanel = new Composite(parent, SWT.NULL);
		buttonPanel.setLayout(new GridLayout(3, true));
		txtEclipse.setText(eclipseValue);
		txtSoftwareFm.setText(softwareFmValue);
		Swts.makePushButton(buttonPanel, resourceGetter, "button.cancel.title", new Runnable() {
			@Override
			public void run() {
				getShell().close();
				
			}
		});
		Button copyToSoftwareFm = Swts.makePushButton(buttonPanel, resourceGetter, "button.copyToSoftwareFm.title", new Runnable() {
			@Override
			public void run() {
				getShell().close();
		
			}
		});
		Button copyToEclipseButton = Swts.makePushButton(buttonPanel, resourceGetter, "button.copyToEclipse.title", new Runnable() {
			@Override
			public void run() {
				getShell().close();
		
			}
		});
		boolean copyToSoftwareFmEnabled = eclipseValue.length() > 0 && !softwareFmValue.equals(eclipseValue);
		copyToSoftwareFm.setEnabled(copyToSoftwareFmEnabled);
		boolean copyToEclipseEnabled = softwareFmValue.length() > 0 && !softwareFmValue.equals(eclipseValue);
		copyToEclipseButton.setEnabled(copyToEclipseEnabled);
	}


	public static void main(String[] args) {

	}

}
