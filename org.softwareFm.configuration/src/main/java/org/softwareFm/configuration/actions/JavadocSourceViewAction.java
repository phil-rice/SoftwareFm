package org.softwareFm.configuration.actions;

import java.text.MessageFormat;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.IAction;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.strings.Strings;

public class JavadocSourceViewAction implements IAction {

	@Override
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) throws Exception {
		if (!Strings.hasValue(actionContext.dataGetter.getDataFor(ConfigurationConstants.dataRawHexDigest)))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.mustHaveA, "hexDigest", getClass().getSimpleName()));

		actionContext.editorFactory.displayEditor(actionContext, displayerDefn, displayer);
	}

}