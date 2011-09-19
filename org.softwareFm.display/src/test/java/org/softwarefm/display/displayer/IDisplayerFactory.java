package org.softwarefm.display.displayer;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwarefm.display.actions.ActionContext;
import org.softwarefm.display.actions.ActionStore;
import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.data.IDataGetter;
import org.softwarefm.display.impl.DisplayerDefn;

public interface IDisplayerFactory {

	IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionStore actionStore, ActionContext actionContext);

	void data(IDataGetter  dataGetter, DisplayerDefn defn, IDisplayer displayer, String entity, String url, Map<String, Object> context, Map<String, Object> data);

}
