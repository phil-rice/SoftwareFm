package org.softwareFm.display.displayer;


import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;

public interface IDisplayerFactory {

	IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionContext actionContext);

	void data(ActionContext actionContext, DisplayerDefn defn, IDisplayer displayer, String entity, String url);

}
