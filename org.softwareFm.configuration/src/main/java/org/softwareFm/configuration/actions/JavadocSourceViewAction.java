package org.softwareFm.configuration.actions;

import java.text.MessageFormat;

import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.IAction;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.strings.Strings;

public class JavadocSourceViewAction implements IAction {

	
	private final String editorKey;

	public JavadocSourceViewAction(String editorKey) {
		this.editorKey = editorKey;
	}

	@Override
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) throws Exception {
		if (!Strings.hasValue(actionContext.dataGetter.getDataFor(ConfigurationConstants.dataRawHexDigest)))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.mustHaveA, "hexDigest", getClass().getSimpleName()));

		actionContext.editorFactory.displayEditor(displayer.getButtonComposite().getShell(), editorKey, displayerDefn, actionContext, actionData, new ICallback<Object>() {
			@Override
			public void process(Object t) throws Exception {
				throw new UnsupportedOperationException();
			}
		}, null);

		// JavadocOrSourceDialog dialog = new JavadocOrSourceDialog(displayer.getControl().getShell(), ConfigurationConstants.dataRawSource, ConfigurationConstants.dataArtifactSource, JdtConstants.sourceMutatorKey);
		// dialog.open(actionContext.compositeConfig, actionContext, actionData);

	}

}
