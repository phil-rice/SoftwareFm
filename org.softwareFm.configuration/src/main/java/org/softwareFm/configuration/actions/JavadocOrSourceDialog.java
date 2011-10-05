package org.softwareFm.configuration.actions;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.smallButtons.IImageButtonListener;
import org.softwareFm.display.smallButtons.SimpleImageButton;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class JavadocOrSourceDialog extends Dialog {

	private Shell shell;
	private TitleAndText txtEclipse;
	private TitleAndText txtSoftwareFm;
	private TitleAndText txtExperiment;
	private final String eclipseKey;
	private final String softwareFmKey;
	private final String mutatorKey;

	public JavadocOrSourceDialog(Shell parent, String eclipseKey, String softwareFmKey, String mutatorKey ) {
		super(parent);
		this.eclipseKey = eclipseKey;
		this.softwareFmKey = softwareFmKey;
		this.mutatorKey = mutatorKey;

	}

	public void open(CompositeConfig compositeConfig, ActionContext actionContext, ActionData actionData) {
		shell = new Shell(getParentShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		createContents(shell, compositeConfig, actionContext, actionData);
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

	private void createContents(Shell parent, CompositeConfig compositeConfig, final ActionContext actionContext, final ActionData actionData) {
		final IDataGetter dataGetter = actionContext.dataGetter;
		IResourceGetter resourceGetter = compositeConfig.resourceGetter;
		String eclipseValue = Strings.nullSafeToString(dataGetter.getDataFor(eclipseKey));
		String softwareFmValue = Strings.nullSafeToString(dataGetter.getDataFor(softwareFmKey));

		txtEclipse = new TitleAndText(compositeConfig, parent, "dialog.eclipseValue.title", true);
		txtEclipse.setText(eclipseValue);
		txtEclipse.setEditable(false);
		addCopyToSoftwareFmButton(compositeConfig, actionContext, actionData, txtEclipse, false);

		txtSoftwareFm = new TitleAndText(compositeConfig, parent, "dialog.softwareFmValue.title", true);
		txtSoftwareFm.setText(softwareFmValue);
		txtSoftwareFm.setEditable(false);
		addCopyToEclipseButton(compositeConfig, dataGetter, txtSoftwareFm, false);

		txtExperiment = new TitleAndText(compositeConfig, parent, "dialog.experiment.title", true);
		addCopyToEclipseButton(compositeConfig, dataGetter, txtExperiment, true);
		addCopyToSoftwareFmButton(compositeConfig, actionContext, actionData, txtExperiment, true);

		Composite buttonPanel = new Composite(parent, SWT.NULL);
		buttonPanel.setLayout(new GridLayout(3, true));
		Swts.makePushButton(buttonPanel, resourceGetter, "button.cancel.title", new Runnable() {
			@Override
			public void run() {
				getShell().close();

			}
		});
	}

	private SimpleImageButton addCopyToEclipseButton(CompositeConfig compositeConfig, final IDataGetter dataGetter, final TitleAndText parent, boolean onlyEnableIfHasValue) {
		SimpleImageButton simpleImageButton = new SimpleImageButton(parent, compositeConfig.imageButtonConfig.withImage(ArtifactsAnchor.jarCopyFromSoftwareFmKey), false);
		simpleImageButton.addListener(new IImageButtonListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void buttonPressed(IHasControl button) throws Exception {
				ICallback<String> javadocMutator = (ICallback<String>) dataGetter.getDataFor(mutatorKey);
				javadocMutator.process(parent.getText());
				getShell().close();
			}
		});
		simpleImageButton.getControl().setEnabled(onlyEnableIfHasValue || parent.getText().length() > 0);
		simpleImageButton.getControl().setToolTipText(Strings.nullSafeToString(dataGetter.getDataFor("button.copyToEclipse.title")));
		return simpleImageButton;

	}

	private SimpleImageButton addCopyToSoftwareFmButton(CompositeConfig compositeConfig, final ActionContext actionContext, final ActionData actionData, final TitleAndText parent, boolean onlyEnableIfHasValue) {
		SimpleImageButton simpleImageButton = new SimpleImageButton(parent, compositeConfig.imageButtonConfig.withImage(ArtifactsAnchor.jarCopyToSoftwareFmKey) ,false);
		simpleImageButton.addListener(new IImageButtonListener() {
			@Override
			public void buttonPressed(IHasControl button) throws Exception {
				actionContext.updateStore.update(actionData, softwareFmKey,  parent.getText());
				getShell().close();
			}
		});
		simpleImageButton.getControl().setEnabled(onlyEnableIfHasValue || parent.getText().length() > 0);
		simpleImageButton.getControl().setToolTipText(Strings.nullSafeToString(actionContext.dataGetter.getDataFor("button.copyToSoftwareFm.title")));
		return simpleImageButton;
	}

	public static void main(String[] args) {
	}

}
