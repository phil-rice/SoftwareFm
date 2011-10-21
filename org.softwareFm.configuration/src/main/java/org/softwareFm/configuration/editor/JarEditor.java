package org.softwareFm.configuration.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import org.softwareFm.utilities.strings.Strings;

public class JarEditor implements IEditor {

	private Composite content;
	private TitleAndText jarPathText;
	private TitleAndText jarNameText;
	private ButtonParent buttonParent;
	private StyledText helpText;

	@Override
	public Control getControl() {
		return content;
	}
	@Override
	public Control createControl(ActionContext actionContext) {
		content = Swts.newComposite(actionContext.rightHandSide.getComposite(), SWT.NULL, getClass().getSimpleName());
		CompositeConfig config = actionContext.compositeConfig;
		jarPathText = new TitleAndText(config, content, ConfigurationConstants.jarPathTitle, true);
		jarNameText = new TitleAndText(config, content, ConfigurationConstants.jarNameTitle, true);
		jarPathText.setEditable(false);
		jarNameText.setEditable(false);
		buttonParent = new ButtonParent(content, config, SWT.NULL);
		helpText = Swts.makeHelpDisplayer(content);
	
		Swts.addGrabHorizontalAndFillGridDataToAllChildrenWithMargins(content, actionContext.compositeConfig.layout.dataMargin);
//		content.setLayout(Swts.getHorizonalNoMarginRowLayout());
//		for (Control child: content.getChildren())
//			child.setLayoutData(new RowData());

		return content;
	}

	
	@Override
	public void edit(IDisplayer parent, DisplayerDefn displayerDefn, ActionContext actionContext, final IEditorCompletion completion) {
		Swts.setHelpText(helpText, actionContext.compositeConfig.resourceGetter, displayerDefn.helpKey);
		Object jarPath = actionContext.dataGetter.getDataFor(ConfigurationConstants.dataRawJarPath);
		Object jarName = actionContext.dataGetter.getDataFor(ConfigurationConstants.dataRawJarName);
		jarPathText.setText(Strings.nullSafeToString(jarPath));
		jarNameText.setText(Strings.nullSafeToString(jarName));
		Swts.addOkCancel(buttonParent, actionContext.compositeConfig, new Runnable() {
			@Override
			public void run() {
				if (completion != null)
					completion.cancel();
			}
		}, new Runnable() {
			@Override
			public void run() {
				if (completion != null)
					completion.cancel();
			}
		}).setOkEnabled(false);
		
	}


	@Override
	public IButtonParent actionButtonParent() {
		return buttonParent;
	}
	public static void main(String[] args) {
		Editors.display(JarEditor.class.getSimpleName(), new JarEditor(), //
				ConfigurationConstants.dataRawJarPath, "some/jar/path/name.jar",//
				ConfigurationConstants.dataRawJarName, "name.jar"//
				);
	}

}
