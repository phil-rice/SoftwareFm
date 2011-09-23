package org.softwareFm.display.displayer;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;

public class ButtonDisplayerFactory implements IDisplayerFactory {

	@Override
	public IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionStore actionStore, ActionContext actionContext) {
		return new ButtonDisplayer(compositeConfig, largeButtonComposite, defn.title, true);
	}

	@Override
	public void data(ActionContext actionContext, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		((ButtonDisplayer) displayer).data(actionContext, defn, entity);
	}

}
