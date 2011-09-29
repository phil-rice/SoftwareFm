package org.softwareFm.display.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.rss.RssDisplay;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class RssFeedConfigurator implements IBrowserConfigurator {

	@Override
	public void configure(CompositeConfig config, IBrowserCompositeBuilder builder) {
		final IResourceGetter resourceGetter = config.resourceGetter;
		builder.register(DisplayConstants.rssFeedType, new IFunction1<Composite, IBrowserPart>() {
			@Override
			public IBrowserPart apply(Composite from) throws Exception {
				return new RssDisplay(from, SWT.NULL, resourceGetter);
			}
		});

	}

}
