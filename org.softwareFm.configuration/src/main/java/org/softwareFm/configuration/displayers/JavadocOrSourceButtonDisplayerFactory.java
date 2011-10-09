package org.softwareFm.configuration.displayers;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.displayer.IDisplayerFactory;

public class JavadocOrSourceButtonDisplayerFactory implements IDisplayerFactory {


	private final String artifactKey;
	private final String artifactEclipseKey;
	private final String artifactSoftwareFmKey;
	private final String eclipseMutatorKey;

	public JavadocOrSourceButtonDisplayerFactory( String artifactKey, String artifactEclipseKey, String artifactSoftwareFmKey, String eclipseMutatorKey) {
		this.artifactKey = artifactKey;
		this.artifactEclipseKey = artifactEclipseKey;
		this.artifactSoftwareFmKey = artifactSoftwareFmKey;
		this.eclipseMutatorKey = eclipseMutatorKey;
	}

	@Override
	public IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionContext actionContext) {
		return new JavadocOrSourceButtonDisplayer(compositeConfig, largeButtonComposite, defn.title, true, artifactKey, artifactEclipseKey, artifactSoftwareFmKey, eclipseMutatorKey);
	}

	@Override
	public void data(ActionContext actionContext, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		((JavadocOrSourceButtonDisplayer) displayer).data(actionContext, defn, entity);
	}

}