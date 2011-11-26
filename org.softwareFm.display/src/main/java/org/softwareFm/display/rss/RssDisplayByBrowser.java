/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.display.rss;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.browser.IBrowserPart;

public class RssDisplayByBrowser implements IBrowserPart {

	private final Browser browser;

	public RssDisplayByBrowser(Composite parent, int style) {
		browser = new Browser(parent, style);
	}

	@Override
	public Composite getComposite() {
		return browser;
	}

	@Override
	public Control getControl() {
		return browser;
	}

	@Override
	public boolean usesUrl() {
		return false;
	}

	@Override
	public void displayUrl(String url) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void displayReply(int statusCode, String reply) throws Exception {
		int index1 = reply.indexOf("<rss");
		int index2 = reply.indexOf("<feed");
		if (index1 == -1 && index2 == -1)
			browser.setText("<h1>Not an RSS Feed</h1>");
		else {
			try {
				String html = new RssFeedTransformer().apply(reply);
				browser.setText(html);
			} catch (Exception e) {
				throw new RuntimeException("\n" + reply + "\n", e);
			}
		}

	}

}