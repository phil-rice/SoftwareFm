package org.softwareFm.display.displayer;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.impl.DisplayerDefn;

public interface IDisplayerFactory {

	IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionStore actionStore, ActionContext actionContext);

	void data(IDataGetter  dataGetter, DisplayerDefn defn, IDisplayer displayer, String entity, String url, Map<String, Object> context, Map<String, Object> data);

}
