package org.softwareFm.display.actions;

import java.util.List;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.collections.Lists;

public class ActionMock implements IAction {

	private final String name;

	public final List<IDisplayer> displayers = Lists.newList();
	public final List<List<String>> formalParams = Lists.newList();

	public ActionMock(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ActionMock [name=" + name + "]";
	}

	@Override
	public void execute(ActionContext actionContext, IDisplayer displayer,int index,  ActionData actionData) {
		displayers.add(displayer);
		formalParams.add(actionData.formalParams);
	}

}
