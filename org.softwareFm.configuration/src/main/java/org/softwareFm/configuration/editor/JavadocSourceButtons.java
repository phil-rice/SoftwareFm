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
	final Button copyFromEclipseToSoftwareFmButton;
	boolean copyToEclipseAndSfmEnabled;
	boolean copyToEclipseEnabled;
	boolean copyToSoftwareFmEnabled;
	boolean copyEclipseToSoftwareFmEnabled;
	private final Button nukeButton;

	public JavadocSourceButtons(IButtonParent buttonParent, CompositeConfig config, Runnable onCancel, final Runnable copyToEclipse, final Runnable copyToSoftwareFm, Runnable copyEclipseToSoftwareFm, Runnable nuke) {
		IResourceGetter resourceGetter = config.resourceGetter;
		Composite buttonComposite = buttonParent.getButtonComposite();
		cancelButton = Swts.makePushButton(buttonComposite, resourceGetter, DisplayConstants.buttonCancelsTitle, onCancel);
		copyToEclipseButton = Swts.makePushButton(buttonComposite, resourceGetter, ConfigurationConstants.buttonCopyToEclipseTitle, copyToEclipse);
		copyToSoftwareFmButton = Swts.makePushButton(buttonComposite, resourceGetter, ConfigurationConstants.buttonCopyToSoftwareFmTitle, copyToSoftwareFm);
		copyToEclipseAndSoftwareFmButton = Swts.makePushButton(buttonComposite, resourceGetter, ConfigurationConstants.buttonCopyToBothTitle, new Runnable() {
			@Override
			public void run() {
				copyToSoftwareFm.run();
				copyToEclipse.run();
			}
		});
		copyFromEclipseToSoftwareFmButton = Swts.makePushButton(buttonComposite, resourceGetter, ConfigurationConstants.buttonCopyEclipseToSoftwareFmTitle, copyEclipseToSoftwareFm);
		nukeButton = Swts.makePushButton(buttonComposite, resourceGetter, "Nuke", false, nuke);
		setLayoutData(config, cancelButton, copyToEclipseButton, copyToSoftwareFmButton);
		buttonComposite.layout();
		buttonComposite.getParent().layout(); // only needed I think if there are no other buttons on the button composite
	}

	private void setLayoutData(CompositeConfig config, Button... buttons) {
		for (Button button : buttons)
			button.setLayoutData(new RowData(config.layout.okCancelWidth, config.layout.okCancelHeight));
	}

	public void setCopyButtonsEnabled(boolean copyToEclipse, boolean copyToSoftwareFm, boolean copyEclipseToSoftwareFm) {
		this.copyToEclipseEnabled = copyToEclipse;
		this.copyToSoftwareFmEnabled = copyToSoftwareFm;
		this.copyEclipseToSoftwareFmEnabled = copyEclipseToSoftwareFm;
		this.copyToEclipseAndSfmEnabled = copyToEclipse && copyToSoftwareFm;
		copyToEclipseAndSoftwareFmButton.setEnabled(copyToEclipseAndSfmEnabled);
		copyToEclipseButton.setEnabled(copyToEclipse);
		copyToSoftwareFmButton.setEnabled(copyToSoftwareFm);
		copyFromEclipseToSoftwareFmButton.setEnabled(copyEclipseToSoftwareFm);
	}

}
