package org.softwareFm.display.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.composites.CompositeConfig;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.functions.IFunction1;

public class BrowserFeedConfigurator implements IBrowserConfigurator {

	@Override
	public void configure(CompositeConfig config, IBrowserCompositeBuilder builder) {
		builder.register(DisplayConstants.browserFeedType, new IFunction1<Composite, IBrowserPart>() {
			@Override
			public IBrowserPart apply(Composite from) throws Exception {
				return new BrowserPart(from, SWT.NULL);
			}
		});

	}

}
