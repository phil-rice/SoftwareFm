package org.softwarefm.display.actions;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwarefm.display.IAction;
import org.softwarefm.display.displayer.IDisplayer;

public class BrowseAction implements IAction {

	@Override
	public void execute(ActionContext actionContext, IDisplayer displayer, int index, List<String> formalParameters, List<Object> actualParameters) {
		System.out.println("Browse execution with: " + formalParameters + " => " + actualParameters);
		Object param = actualParameters.get(0);
		if (param != null) {
			try {
				Desktop.getDesktop().browse(new URI(param.toString()));
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}
	}

}
