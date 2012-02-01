/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.rss;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.swt.Marker;
import org.softwareFm.swt.swt.ISituationListAndBuilder;
import org.softwareFm.swt.swt.Swts.Show;

public class RssPersonUnit {
	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.display/src/test/resources/org/softwareFm/display/rss");
		Show.xUnit("Rss Person Unit", root, "xml", //
				new ISituationListAndBuilder<RssDisplay>() {

					@Override
					public RssDisplay makeChild(Composite from) {
						IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(Marker.class, "Test");
						RssDisplay rssDisplay = new RssDisplay(from, SWT.BORDER, resourceGetter);
						return rssDisplay;
					}

					@Override
					public void selected(RssDisplay hasControl, String context, Object value) throws Exception {
						hasControl.displayReply(200, value.toString());
					}
				});
	}

}