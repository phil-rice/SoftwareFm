/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.browser;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BrowserPart implements IBrowserPart {

	private final Browser browser;

	public BrowserPart(Composite parent, int style) {
		browser = new Browser(parent, style);
	}

	@Override
	public Control getControl() {
		return browser;
	}

	@Override
	public boolean usesUrl() {
		return true;
	}

	@Override
	public void displayUrl(String url) {
		browser.setUrl(url);

	}

	@Override
	public void displayReply(int statusCode, String reply) {
		browser.setText(reply);
	}

	@Override
	public Composite getComposite() {
		return browser;
	}

}