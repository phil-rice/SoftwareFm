package org.softwareFm.configuration.displayers;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.actions.ActionContext;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.data.IDataGetter;
import org.softwareFm.display.displayer.CompressedText;
import org.softwareFm.display.displayer.DisplayerDefn;
import org.softwareFm.display.displayer.IDisplayer;
import org.softwareFm.display.displayer.IDisplayerFactory;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class JavadocSourceSummaryDisplayerFactory implements IDisplayerFactory {

	private final String artifactEclipseKey;
	private final String artifactSoftwareFmKey;

	public JavadocSourceSummaryDisplayerFactory(String artifactEclipseKey, String artifactSoftwareFmKey) {
		this.artifactEclipseKey = artifactEclipseKey;
		this.artifactSoftwareFmKey = artifactSoftwareFmKey;
	}

	@Override
	public IDisplayer create(Composite largeButtonComposite, DisplayerDefn defn, int style, CompositeConfig compositeConfig, ActionContext actionContext) {
		return new CompressedText(largeButtonComposite, style, compositeConfig);
	}

	@Override
	public void data(ActionContext actionContext, DisplayerDefn defn, IDisplayer displayer, String entity, String url) {
		try {
			final IDataGetter dataGetter = actionContext.dataGetter;
			final String eclipseValue = Strings.nullSafeToString(dataGetter.getDataFor(artifactEclipseKey));
			final String softwareFmValue = Strings.nullSafeToString(dataGetter.getDataFor(artifactSoftwareFmKey));

			JavadocOrSourceState state = new JavadocOrSourceState(eclipseValue, softwareFmValue);
			JavadocOrSourceButtonTitleCalculator calculator = new JavadocOrSourceButtonTitleCalculator(null, null);
			String titleKey = calculator.apply(state).title;
			String title = IResourceGetter.Utils.getOrException(actionContext.compositeConfig.resourceGetter, titleKey);
			((CompressedText) displayer).setText(title);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

}