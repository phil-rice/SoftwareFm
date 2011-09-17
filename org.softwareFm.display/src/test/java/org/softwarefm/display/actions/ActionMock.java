package org.softwarefm.display.actions;

import java.util.List;

import org.softwarefm.display.IAction;
import org.softwarefm.display.displayer.IDisplayer;

public class ActionMock implements IAction {

	private final String name;

	public ActionMock(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ActionMock [name=" + name + "]";
	}

	@Override
	public void execute(IDisplayer displayer, List<String> formalParameters, List<Object> actualParameters) {
		throw new UnsupportedOperationException();
	}

}
