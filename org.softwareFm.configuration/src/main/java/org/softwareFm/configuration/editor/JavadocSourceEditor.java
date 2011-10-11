package org.softwareFm.configuration.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.editor.Editors;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.display.editor.IEditorCompletion;
import org.softwareFm.display.simpleButtons.ButtonParent;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.smallButtons.IImageButtonListener;
import org.softwareFm.display.smallButtons.SimpleImageButton;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class JavadocSourceEditor implements IEditor {
	private IEditorCompletion completion;
	private Composite content;
	private TitleAndText txtEclipse;
	private TitleAndText txtSoftwareFm;
	private TitleAndText txtExperiment;
	private final String eclipseKey;
	private final String softwareFmKey;
	private final String mutatorKey;
	private final String titleKey;
	private ButtonParent buttonParent;
	private Runnable okRunnable;

	public JavadocSourceEditor(String titleKey, String eclipseKey, String softwareFmKey, String mutatorKey) {
		this.titleKey = titleKey;
		this.eclipseKey = eclipseKey;
		this.softwareFmKey = softwareFmKey;
		this.mutatorKey = mutatorKey;
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Control createControl(ActionContext actionContext) {
		content = Swts.newComposite(actionContext.rightHandSide.getComposite(), SWT.NULL, getClass().getSimpleName());
		final IDataGetter dataGetter = actionContext.dataGetter;
		CompositeConfig config = actionContext.compositeConfig;

		new Label(content, SWT.NULL).setText(IResourceGetter.Utils.getOrException(actionContext.compositeConfig.resourceGetter, titleKey));
		buttonParent = new ButtonParent(content, config, SWT.NULL);

		txtEclipse = new TitleAndText(config, content, "dialog.eclipseValue.title", true);
		txtEclipse.setEditable(false);
		addCopyToSoftwareFmButton(config, actionContext, txtEclipse, false);

		txtSoftwareFm = new TitleAndText(config, content, "dialog.softwareFmValue.title", true);
		txtSoftwareFm.setEditable(false);
		addCopyToEclipseButton(config, dataGetter, txtSoftwareFm, false);

		txtExperiment = new TitleAndText(config, content, "dialog.experiment.title", true);
		addCopyToEclipseButton(config, dataGetter, txtExperiment, true);
		addCopyToSoftwareFmButton(config, actionContext, txtExperiment, true);

		Composite buttonPanel = new Composite(content, SWT.NULL);
		buttonPanel.setLayout(new GridLayout(3, true));
		okRunnable = new Runnable() {
			@Override
			public void run() {
				sendResult();
			}
		};
		Swts.makeAcceptCancelComposite(content, SWT.NULL, actionContext.compositeConfig.resourceGetter, okRunnable, new Runnable() {
			@Override
			public void run() {
				cancel();
			}
		});
		Swts.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(content, actionContext.compositeConfig.layout.dataMargin);
		return content;
	}

	private SimpleImageButton addCopyToEclipseButton(CompositeConfig compositeConfig, final IDataGetter dataGetter, final TitleAndText parent, boolean onlyEnableIfHasValue) {
		SimpleImageButton simpleImageButton = new SimpleImageButton(parent, compositeConfig.imageButtonConfig.withImage(ArtifactsAnchor.jarCopyFromSoftwareFmKey), false);
		simpleImageButton.addListener(new IImageButtonListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void buttonPressed(IHasControl button) throws Exception {
				ICallback<String> javadocMutator = (ICallback<String>) dataGetter.getDataFor(mutatorKey);
				javadocMutator.process(parent.getText());
				cancel();
			}
		});
		simpleImageButton.getControl().setEnabled(onlyEnableIfHasValue || parent.getText().length() > 0);
		simpleImageButton.getControl().setToolTipText(Strings.nullSafeToString(dataGetter.getDataFor("button.copyToEclipse.title")));
		return simpleImageButton;

	}

	private SimpleImageButton addCopyToSoftwareFmButton(CompositeConfig compositeConfig, final ActionContext actionContext, final TitleAndText parent, boolean onlyEnableIfHasValue) {
		SimpleImageButton simpleImageButton = new SimpleImageButton(parent, compositeConfig.imageButtonConfig.withImage(ArtifactsAnchor.jarCopyToSoftwareFmKey), false);
		simpleImageButton.addListener(new IImageButtonListener() {
			@Override
			public void buttonPressed(IHasControl button) throws Exception {
				if (okRunnable != null)
					okRunnable.run();
			}
		});
		simpleImageButton.getControl().setEnabled(onlyEnableIfHasValue || parent.getText().length() > 0);
		simpleImageButton.getControl().setToolTipText(Strings.nullSafeToString(actionContext.dataGetter.getDataFor("button.copyToSoftwareFm.title")));
		return simpleImageButton;
	}

	@Override
	public void edit(IDisplayer parent, DisplayerDefn displayerDefn, ActionContext actionContext, IEditorCompletion completion) {
		IDataGetter dataGetter = actionContext.dataGetter;
		this.completion = completion;

		String eclipseValue = Strings.nullSafeToString(dataGetter.getDataFor(eclipseKey));
		txtEclipse.setText(eclipseValue);

		String softwareFmValue = Strings.nullSafeToString(dataGetter.getDataFor(softwareFmKey));
		txtSoftwareFm.setText(softwareFmValue);
	}

	private void cancel() {
		completion.cancel();
		okRunnable = null;
	}

	private void sendResult() {
		completion.cancel();
		okRunnable = null;
	}

	@Override
	public IButtonParent actionButtonParent() {
		return buttonParent;
	}

	public static void main(String[] args) {
		Editors.display(JavadocSourceEditor.class.getSimpleName(),//
				new JavadocSourceEditor(ConfigurationConstants.javadocKey, ConfigurationConstants.dataRawJavadoc, ConfigurationConstants.dataArtifactJavadoc, ConfigurationConstants.dataRawJavadocMutator), //
				ConfigurationConstants.dataRawJavadoc, "rawJavadoc",//
				ConfigurationConstants.dataArtifactJavadoc, "SfmJavadoc"
				);
	}
}
