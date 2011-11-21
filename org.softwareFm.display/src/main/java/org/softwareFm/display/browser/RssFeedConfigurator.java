package org.softwareFm.display.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.rss.RssDisplayByBrowser;
import org.softwareFm.utilities.functions.IFunction1;

public class RssFeedConfigurator implements IBrowserConfigurator {

	@Override
	public void configure(IBrowserCompositeBuilder builder) {
		builder.register(DisplayConstants.rssFeedType, new IFunction1<Composite, IBrowserPart>() {
			@Override
			public IBrowserPart apply(Composite from) throws Exception {
				return new RssDisplayByBrowser(from, SWT.NULL);
			}
		});

	}

}
