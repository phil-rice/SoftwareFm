package org.softwareFm.configuration.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.editor.EditorContext;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.display.editor.IEditorCompletion;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.maps.Maps;

public class SoftwareFmIdEditor implements IEditor {

	private IEditorCompletion completion;
	private Composite composite;
	private TitleAndText groupIdText;
	private TitleAndText artifactIdText;
	private TitleAndText versionText;

	@Override
	public Control getControl() {
		return null;
	}

	@Override
	public Control createControl(ActionContext actionContext) {
		composite = Swts.newComposite(actionContext.rightHandSide.getComposite(), SWT.NULL, getClass().getSimpleName());
		groupIdText = new TitleAndText(actionContext.compositeConfig, composite, ConfigurationConstants.groupIdTitle, true);
		artifactIdText = new TitleAndText(actionContext.compositeConfig, composite, ConfigurationConstants.artifactIdTitle, true);
		versionText = new TitleAndText(actionContext.compositeConfig, composite, ConfigurationConstants.versionTitle, true);
		addCrListeners(groupIdText, artifactIdText, versionText);
		Swts.makeAcceptCancelComposite(composite, SWT.NULL, actionContext.compositeConfig.resourceGetter, new Runnable() {
			@Override
			public void run() {
				sendResult();
			}
		}, new Runnable() {
			@Override
			public void run() {
				completion.cancel();
			}
		});
		Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
		return composite;
	}

	private void addCrListeners(TitleAndText... texts) {
		for (final TitleAndText text : texts)
			text.addCrListener(new Listener() {
				@Override
				public void handleEvent(Event event) {
					sendResult();
				}
			});

	}

	@Override
	public void edit(Shell parent, DisplayerDefn displayerDefn, EditorContext editorContext, ActionContext actionContext, ActionData actionData, IEditorCompletion completion, Object initialValue) {
		this.completion = completion;
	}

	@Override
	public void cancel() {
		completion.cancel();
	}

	private void sendResult() {
		completion.ok(Maps.<String, Object> makeMap(//
				ConfigurationConstants.groupId, groupIdText.getText(),//
				ConfigurationConstants.artifactId, artifactIdText.getText(),//
				ConfigurationConstants.version, versionText.getText()));
	}

}
