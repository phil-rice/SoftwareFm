package org.softwareFm.configuration.actions;

import java.util.Map;

import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.IAction;
import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.callbacks.ICallback;

public class NukeJavadocAndSourceAction implements IAction {

	public NukeJavadocAndSourceAction() {
	}

	@Override
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) throws Exception {
		nuke(actionContext, "javadoc");
		nuke(actionContext, "source");
	}

	@SuppressWarnings("unchecked")
	private void nuke(ActionContext actionContext, String artifact) throws Exception {
		IDataGetter dataGetter = actionContext.dataGetter;
		ICallback<String> callback = (ICallback<String>) dataGetter.getDataFor("data.raw.jar." + artifact + "Mutator");
		callback.process("");
		Map<String, Object> rawData = (Map<String, Object>) dataGetter.getLastRawData("jar");
		rawData.put(artifact, "");
		dataGetter.setRawData("jar", rawData);
	}

}
