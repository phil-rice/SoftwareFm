package org.softwareFm.display.displayer;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;

public class DisplayerMock implements IDisplayerFactory {

	private final String seed;

	public DisplayerMock(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "DisplayerMock [seed=" + seed + "]";
	}

	@Override
	public IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionStore actionStore, ActionContext actionContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void data(IDataGetter dataGetter, DisplayerDefn defn, IDisplayer displayer, String entity, String url, Map<String, Object> context, Map<String, Object> data) {
		throw new UnsupportedOperationException();
	}

}
