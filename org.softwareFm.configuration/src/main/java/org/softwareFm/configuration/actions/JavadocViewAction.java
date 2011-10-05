package org.softwareFm.configuration.actions;

import java.text.MessageFormat;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.IAction;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.jdtBinding.api.JdtConstants;
import org.softwareFm.utilities.strings.Strings;

public class JavadocViewAction implements IAction {


	@Override
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) throws Exception {
		if (!Strings.hasValue(actionContext.dataGetter.getDataFor(ConfigurationConstants.dataRawHexDigest)))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.mustHaveA, "hexDigest", getClass().getSimpleName()));
		JavadocOrSourceDialog dialog = new JavadocOrSourceDialog(displayer.getControl().getShell(), ConfigurationConstants.dataRawJavadoc, ConfigurationConstants.dataArtifactJavadoc, JdtConstants.javadocMutatorKey);
		dialog.open(actionContext.compositeConfig, actionContext, actionData);
	}

}
