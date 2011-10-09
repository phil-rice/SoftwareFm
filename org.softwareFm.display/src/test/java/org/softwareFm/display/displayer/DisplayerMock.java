package org.softwareFm.display.displayer;


import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;

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
	public IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionContext actionContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void data(ActionContext actionContext, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		throw new UnsupportedOperationException();
	}


}
