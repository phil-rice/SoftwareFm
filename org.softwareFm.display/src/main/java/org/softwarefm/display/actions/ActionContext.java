package org.softwarefm.display.actions;

import org.softwarefm.display.composites.CompositeConfig;
import org.softwarefm.display.data.IDataGetter;

public class ActionContext {
	public final IDataGetter dataGetter;
	public final CompositeConfig compositeConfig;
	public ActionContext(IDataGetter dataGetter, CompositeConfig compositeConfig) {
		super();
		this.dataGetter = dataGetter;
		this.compositeConfig = compositeConfig;
	}


}
