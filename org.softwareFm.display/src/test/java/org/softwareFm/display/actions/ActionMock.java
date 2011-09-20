package org.softwareFm.display.actions;

import java.util.List;

import org.softwareFm.display.IAction;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.utilities.collections.Lists;

public class ActionMock implements IAction {

	private final String name;

	public final List<IDisplayer> displayers = Lists.newList();
	public final List<List<String>> formalParams = Lists.newList();
	public final List<List<Object>> actualParams = Lists.newList();

	public ActionMock(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ActionMock [name=" + name + "]";
	}

	@Override
	public void execute(ActionContext actionContext, IDisplayer displayer,int index,  List<String> formalParameters, List<Object> actualParameters) {
		displayers.add(displayer);
		formalParams.add(formalParameters);
		actualParams.add(actualParameters);
	}

}
