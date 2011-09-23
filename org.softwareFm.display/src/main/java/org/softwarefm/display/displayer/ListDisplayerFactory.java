package org.softwareFm.display.displayer;


import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;

public class ListDisplayerFactory implements IDisplayerFactory {

	@Override
	public IDisplayer create(Composite composite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionStore actionStore, ActionContext actionContext) {
		return new ListDisplayer(composite, defn, style, compositeConfig, actionStore, actionContext);
	}

	@Override
	public void data(ActionContext actionContext, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		((ListDisplayer) displayer).data(actionContext.dataGetter, defn, entity, url);
	}

}
