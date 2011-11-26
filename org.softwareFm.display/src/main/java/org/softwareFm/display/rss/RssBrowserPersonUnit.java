/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.display.rss;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts.Show;

public class RssBrowserPersonUnit {
	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.display/src/test/resources/org/softwareFm/display/rss");
		Show.xUnit("Rss Person Unit", root, "xml", //
				new ISituationListAndBuilder<RssDisplayByBrowser>() {

					@Override
					public RssDisplayByBrowser makeChild(Composite from) {
						RssDisplayByBrowser rssDisplay = new RssDisplayByBrowser(from, SWT.BORDER);
						return rssDisplay;
					}

					@Override
					public void selected(RssDisplayByBrowser hasControl, String context, Object value) throws Exception {
						hasControl.displayReply(200, value.toString());
					}
				});
	}

}