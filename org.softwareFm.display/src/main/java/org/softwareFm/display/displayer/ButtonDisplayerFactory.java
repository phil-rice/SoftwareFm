package org.softwareFm.display.displayer;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;

public class ButtonDisplayerFactory implements IDisplayerFactory {

	@Override
	public IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionStore actionStore, ActionContext actionContext) {
		return new ButtonDisplayer(compositeConfig, largeButtonComposite, defn.title, true);
	}

	@Override
	public void data(IDataGetter dataGetter, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		((ButtonDisplayer) displayer).data(dataGetter, defn, entity);
	}

}
