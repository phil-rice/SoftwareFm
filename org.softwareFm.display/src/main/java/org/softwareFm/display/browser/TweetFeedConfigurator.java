package org.softwareFm.display.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.functions.IFunction1;

public class TweetFeedConfigurator implements IBrowserConfigurator {

	@Override
	public void configure(IBrowserCompositeBuilder builder) {
		builder.register(DisplayConstants.tweetFeedType, new IFunction1<Composite, IBrowserPart>() {
			@Override
			public IBrowserPart apply(Composite from) throws Exception {
				return new BrowserPart(from, SWT.NULL){
					@Override
					public void displayUrl(String url) {
						super.displayUrl("http://mobile.twitter.com/" + url);
					}
				};
			}
		});

	}

}
