package org.softwarefm.display.actions;

import org.softwarefm.display.IAction;

public class ActionMock implements IAction {

	private final String name;

	public ActionMock(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ActionMock [name=" + name + "]";
	}

}
