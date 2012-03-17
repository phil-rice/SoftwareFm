/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.swt.constants.DisplayConstants;

public class TweetFeedConfigurator implements IBrowserConfigurator {

	@Override
	public void configure(IBrowserCompositeBuilder builder) {
		builder.register(DisplayConstants.tweetFeedType, new IFunction1<Composite, IBrowserPart>() {
			@Override
			public IBrowserPart apply(Composite from) throws Exception {
				return new BrowserPart(from, SWT.NULL) {
					@Override
					public void displayUrl(String url) {
						super.displayUrl("http://mobile.twitter.com/" + url);
					}
				};
			}
		});

	}

}