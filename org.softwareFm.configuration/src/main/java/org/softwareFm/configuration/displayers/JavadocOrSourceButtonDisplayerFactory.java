package org.softwareFm.configuration.displayers;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.actions.ActionStore;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.displayer.IDisplayerFactory;

public class JavadocOrSourceButtonDisplayerFactory implements IDisplayerFactory {

	private final String artifact;

	public JavadocOrSourceButtonDisplayerFactory(String artifact) {
		this.artifact = artifact;
	}

	@Override
	public IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionStore actionStore, ActionContext actionContext) {
		return new JavadocOrSourceButtonDisplayer(compositeConfig, largeButtonComposite, defn.title, true, artifact);
	}

	@Override
	public void data(IDataGetter dataGetter, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		((JavadocOrSourceButtonDisplayer) displayer).data(dataGetter, defn, entity);
	}

}