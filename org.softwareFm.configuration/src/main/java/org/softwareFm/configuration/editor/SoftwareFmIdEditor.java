package org.softwareFm.configuration.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.composites.TitleAndText;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.editor.Editors;
import org.softwareFm.display.editor.IEditor;
import org.softwareFm.display.editor.IEditorCompletion;
import org.softwareFm.display.simpleButtons.ButtonParent;
import org.softwareFm.display.simpleButtons.IButtonParent;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class SoftwareFmIdEditor implements IEditor {

	private Composite content;
	private TitleAndText groupIdText;
	private TitleAndText artifactIdText;
	private TitleAndText versionText;
	private ButtonParent buttonParent;
	private IEditorCompletion completion;

	@Override
	public Control getControl() {
		return content;
	}
	@Override
	public Control createControl(ActionContext actionContext) {
		content = Swts.newComposite(actionContext.rightHandSide.getComposite(), SWT.NULL, getClass().getSimpleName());
		CompositeConfig config = actionContext.compositeConfig;
		groupIdText = new TitleAndText(config, content, ConfigurationConstants.groupIdTitle, true);
		artifactIdText = new TitleAndText(config, content, ConfigurationConstants.artifactIdTitle, true);
		versionText = new TitleAndText(config, content, ConfigurationConstants.versionTitle, true);
		addCrListeners(groupIdText, artifactIdText, versionText);
		buttonParent = new ButtonParent(content, config, SWT.NULL);
		Swts.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(content, actionContext.compositeConfig.layout.dataMargin);
//		content.setLayout(Swts.getHorizonalNoMarginRowLayout());
//		for (Control child: content.getChildren())
//			child.setLayoutData(new RowData());

		return content;
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
	public void edit(IDisplayer parent, DisplayerDefn displayerDefn, ActionContext actionContext, final IEditorCompletion completion) {
		this.completion = completion;
		Swts.layoutDump(content);
		Object groupId = actionContext.dataGetter.getDataFor(ConfigurationConstants.dataJarGroupId);
		Object artifactId = actionContext.dataGetter.getDataFor(ConfigurationConstants.dataJarArtifactId);
		Object versionId = actionContext.dataGetter.getDataFor(ConfigurationConstants.dataJarVersion);
		groupIdText.setText(Strings.nullSafeToString(groupId));
		artifactIdText.setText(Strings.nullSafeToString(artifactId));
		versionText.setText(Strings.nullSafeToString(versionId));
		Swts.addAcceptCancel(buttonParent, actionContext.compositeConfig, new Runnable() {
			@Override
			public void run() {
				sendResult();
			}
		}, new Runnable() {
			@Override
			public void run() {
				if (completion != null)
					completion.cancel();
			}
		});
	}

	private void sendResult() {
		completion.ok(Maps.<String, Object> makeMap(//
				ConfigurationConstants.groupId, groupIdText.getText(),//
				ConfigurationConstants.artifactId, artifactIdText.getText(),//
				ConfigurationConstants.version, versionText.getText()));
	}

	@Override
	public IButtonParent actionButtonParent() {
		return buttonParent;
	}
	public static void main(String[] args) {
		Editors.display(SoftwareFmIdEditor.class.getSimpleName(), new SoftwareFmIdEditor(), //
				ConfigurationConstants.dataJarGroupId, "GroupId",//
				ConfigurationConstants.dataJarArtifactId, "ArtifactId",//
				ConfigurationConstants.dataJarVersion, "Version"//
				);
	}

}
