package org.softwareFm.display.actions;

import java.awt.Desktop;
import java.net.URI;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.exceptions.WrappedException;

public class BrowseAction implements IAction {

	@Override
	public void execute(ActionContext actionContext, DisplayerDefn displayerDefn, IDisplayer displayer, int index, ActionData actionData) {
		System.out.println("Browse execution with: " + actionData);
		Object param = actionData.actualParams.get(0);
		if (param != null) {
			try {
				Desktop.getDesktop().browse(new URI(param.toString()));
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}
	}

}
