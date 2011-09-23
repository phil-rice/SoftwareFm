package org.softwareFm.configuration.actions;

import java.text.MessageFormat;

import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.IAction;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.strings.Strings;

public class JavadocOrSourceViewAction implements IAction {

	private final String artifact;

	public JavadocOrSourceViewAction(String artifact) {
		this.artifact = artifact;
	}

	@Override
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) throws Exception {
		if (!Strings.hasValue(actionContext.dataGetter.getDataFor("data.raw.jar.hexDigest")))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.mustHaveA, "hexDigest", getClass().getSimpleName()));
		JavadocOrSourceDialog dialog = new JavadocOrSourceDialog(displayer.getControl().getShell(), artifact);
		dialog.open(actionContext.compositeConfig, actionContext.dataGetter);
	}

}
