package org.softwarefm.display.actions;

import java.util.List;

import org.softwarefm.display.IAction;
import org.softwarefm.display.displayer.IDisplayer;

public class BrowseAction implements IAction {

	@Override
	public void execute(IDisplayer displayer, List<String> formalParameters, List<Object> actualParameters) {
		System.out.println("Browse execution with: " + formalParameters +" => "+ actualParameters);
	}



}
