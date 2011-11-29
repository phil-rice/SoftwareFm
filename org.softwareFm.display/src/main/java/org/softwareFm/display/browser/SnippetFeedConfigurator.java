package org.softwareFm.display.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.functions.IFunction1;

public class SnippetFeedConfigurator  implements IBrowserConfigurator{
	@Override
	public void configure(IBrowserCompositeBuilder builder) {
		builder.register(DisplayConstants.snippetFeedType, new IFunction1<Composite, IBrowserPart>() {
			@Override
			public IBrowserPart apply(Composite from) throws Exception {
				return new BrowserPart(from, SWT.NULL){
				};
			}
		});

	}

}
