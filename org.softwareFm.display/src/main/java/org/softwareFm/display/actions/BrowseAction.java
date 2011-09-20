package org.softwareFm.display.actions;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

import org.softwareFm.display.IAction;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.exceptions.WrappedException;

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
