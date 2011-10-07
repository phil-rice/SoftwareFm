package org.softwareFm.display.displayer;

import org.softwareFm.display.actions.ActionContext;

public abstract class ButtonDisplayerFactory implements IDisplayerFactory {


	@Override
	public void data(ActionContext actionContext, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		((ButtonDisplayer) displayer).data(actionContext, defn, entity);
	}

}
