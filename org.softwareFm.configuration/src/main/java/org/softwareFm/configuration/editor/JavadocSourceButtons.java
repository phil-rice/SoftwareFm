package org.softwareFm.configuration.editor;

import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;

public class JavadocSourceButtons {

	private final Button cancelButton;
	final Button copyToEclipseButton;
	final Button copyToSoftwareFmButton;
	final Button copyToEclipseAndSoftwareFmButton;
	private final Button testButton;
	private String testResult;
	boolean copyToEclipseAndSfmEnabled;
	boolean copyToEclipseEnabled;
	boolean copyToSoftwareFmEnabled;

	public JavadocSourceButtons(IButtonParent buttonParent, CompositeConfig config, Runnable onCancel, final ITester tester, Runnable copyToEclipse, Runnable copyToSoftwareFm) {
		IResourceGetter resourceGetter = config.resourceGetter;
		Composite buttonComposite = buttonParent.getButtonComposite();
		cancelButton = Swts.makePushButton(buttonComposite, resourceGetter, DisplayConstants.buttonCancelsTitle, onCancel);
		testButton = Swts.makePushButton(buttonComposite, resourceGetter, ConfigurationConstants.buttonTestTitle, new Runnable() {
			@Override
			public void run() {
				testResult = ITester.Utils.test(tester);
			}
		});
		copyToEclipseButton = Swts.makePushButton(buttonComposite, resourceGetter, ConfigurationConstants.buttonCopyToEclipseTitle, copyToEclipse);
		copyToSoftwareFmButton = Swts.makePushButton(buttonComposite, resourceGetter, ConfigurationConstants.buttonCopyToSoftwareFmTitle, copyToSoftwareFm);
		copyToEclipseAndSoftwareFmButton = Swts.makePushButton(buttonComposite, resourceGetter, ConfigurationConstants.buttonCopyToBothTitle, copyToSoftwareFm);
		setLayoutData(config, cancelButton, copyToEclipseButton, copyToSoftwareFmButton);
		buttonComposite.layout();
		buttonComposite.getParent().layout(); // only needed I think if there are no other buttons on the button composite
	}

	private void setLayoutData(CompositeConfig config, Button... buttons) {
		for (Button button : buttons)
			button.setLayoutData(new RowData(config.layout.okCancelWidth, config.layout.okCancelHeight));
	}

	public void setCopyButtonsEnabled(boolean copyToEclipse, boolean copyToSoftwareFm) {
		this.copyToEclipseEnabled = copyToEclipse;
		this.copyToSoftwareFmEnabled = copyToSoftwareFm;
		this.copyToEclipseAndSfmEnabled = copyToEclipse && copyToSoftwareFm;
		copyToEclipseAndSoftwareFmButton.setEnabled(copyToEclipseAndSfmEnabled);
		copyToEclipseButton.setEnabled(copyToEclipse);
		copyToSoftwareFmButton.setEnabled(copyToSoftwareFm);
	}

}
