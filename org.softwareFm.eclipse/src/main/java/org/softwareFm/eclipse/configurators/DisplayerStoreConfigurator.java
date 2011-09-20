package org.softwareFm.eclipse.configurators;

import org.softwareFm.display.displayer.DisplayerStore;
import org.softwareFm.display.displayer.IDisplayerStoreConfigurator;
import org.softwareFm.display.displayer.ListDisplayerFactory;
import org.softwareFm.display.displayer.TextDisplayerFactory;

public class DisplayerStoreConfigurator implements IDisplayerStoreConfigurator {

	@Override
	public void process(DisplayerStore displayerStore) throws Exception {
		displayerStore.//
				displayer("displayer.text", new TextDisplayerFactory()).//
				displayer("displayer.url", new TextDisplayerFactory()).//
				displayer("displayer.list", new ListDisplayerFactory());
	}

}
