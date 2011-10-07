package org.softwareFm.configuration.editor;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.editor.EditorContext;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.display.editor.IEditorCompletion;
import org.softwareFm.display.smallButtons.IImageButtonListener;
import org.softwareFm.display.smallButtons.SimpleImageButton;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.softwareFmImages.artifacts.ArtifactsAnchor;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.strings.Strings;

public class JavadocSourceEditor implements IEditor {
	private IEditorCompletion completion;
	private Composite contents;
	private TitleAndText txtEclipse;
	private TitleAndText txtSoftwareFm;
	private TitleAndText txtExperiment;
	private final String eclipseKey;
	private final String softwareFmKey;
	private final String mutatorKey;

	public JavadocSourceEditor(String eclipseKey, String softwareFmKey, String mutatorKey) {
		this.eclipseKey = eclipseKey;
		this.softwareFmKey = softwareFmKey;
		this.mutatorKey = mutatorKey;
	}

	@Override
	public Control getControl() {
		return contents;
	}

	@Override
	public Control createControl(ActionContext actionContext, ActionData actionData) {
		contents = Swts.newComposite(actionContext.rightHandSide.getComposite(), SWT.NULL, getClass().getSimpleName());
		final IDataGetter dataGetter = actionContext.dataGetter;
		CompositeConfig compositeConfig = actionContext.compositeConfig;


		txtEclipse = new TitleAndText(compositeConfig, contents, "dialog.eclipseValue.title", true);
		txtEclipse.setEditable(false);
		addCopyToSoftwareFmButton(compositeConfig, actionContext, actionData, txtEclipse, false);

		txtSoftwareFm = new TitleAndText(compositeConfig, contents, "dialog.softwareFmValue.title", true);
		txtSoftwareFm.setEditable(false);
		addCopyToEclipseButton(compositeConfig, dataGetter, txtSoftwareFm, false);

		txtExperiment = new TitleAndText(compositeConfig, contents, "dialog.experiment.title", true);
		addCopyToEclipseButton(compositeConfig, dataGetter, txtExperiment, true);
		addCopyToSoftwareFmButton(compositeConfig, actionContext, actionData, txtExperiment, true);

		Composite buttonPanel = new Composite(contents, SWT.NULL);
		buttonPanel.setLayout(new GridLayout(3, true));
		Swts.makeAcceptCancelComposite(contents, SWT.NULL, actionContext.compositeConfig.resourceGetter, new Runnable() {
			@Override
			public void run() {
				sendResult();
			}
		}, new Runnable() {
			@Override
			public void run() {
					cancel();
			}
		});
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(contents);
		return contents;
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

	private SimpleImageButton addCopyToSoftwareFmButton(CompositeConfig compositeConfig, final ActionContext actionContext, final ActionData actionData, final TitleAndText parent, boolean onlyEnableIfHasValue) {
		SimpleImageButton simpleImageButton = new SimpleImageButton(parent, compositeConfig.imageButtonConfig.withImage(ArtifactsAnchor.jarCopyToSoftwareFmKey) ,false);
		simpleImageButton.addListener(new IImageButtonListener() {
			@Override
			public void buttonPressed(IHasControl button) throws Exception {
				actionContext.updateStore.update(actionData, softwareFmKey,  parent.getText());
				cancel();
			}
		});
		simpleImageButton.getControl().setEnabled(onlyEnableIfHasValue || parent.getText().length() > 0);
		simpleImageButton.getControl().setToolTipText(Strings.nullSafeToString(actionContext.dataGetter.getDataFor("button.copyToSoftwareFm.title")));
		return simpleImageButton;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void edit(Shell parent, DisplayerDefn displayerDefn, EditorContext editorContext, ActionContext actionContext, ActionData actionData, IEditorCompletion completion, Object initialValue) {
		IDataGetter dataGetter = actionContext.dataGetter;
		this.completion = completion;
		Map<String, Object> initialMap = (Map<String, Object>) initialValue;
		
		String eclipseValue = Strings.nullSafeToString(dataGetter.getDataFor(eclipseKey));
		txtEclipse.setText(eclipseValue);

		String softwareFmValue = Strings.nullSafeToString(dataGetter.getDataFor(softwareFmKey));
		txtSoftwareFm.setText(softwareFmValue);
	}

	@Override
	public void cancel() {
		if (completion != null)
			completion.cancel();
	}

	private void sendResult() {
		completion.cancel();
	}
}
