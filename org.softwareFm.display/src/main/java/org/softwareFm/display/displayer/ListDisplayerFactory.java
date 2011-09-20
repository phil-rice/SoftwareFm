package org.softwareFm.display.displayer;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;

public class ListDisplayerFactory implements IDisplayerFactory {

	@Override
	public IDisplayer create(Composite composite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionStore actionStore, ActionContext actionContext) {
		return new ListDisplayer(composite, defn, style, compositeConfig, actionStore, actionContext);
	}

	@Override
	public void data(IDataGetter dataGetter, DisplayerDefn defn, IDisplayer displayer, String entity, String url, Map<String, Object> context, Map<String, Object> data) {
		((ListDisplayer) displayer).data(dataGetter, defn, entity, url, context, data);
	}

}
