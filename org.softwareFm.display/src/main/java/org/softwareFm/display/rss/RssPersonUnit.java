package org.softwareFm.display.rss;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.AllSoftwareFmDisplayTests;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.resources.IResourceGetter;

public class RssPersonUnit {
	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.display/src/test/resources/org/softwareFm/display/rss");
		Swts.xUnit("Rss Person Unit", root, "xml", //
				new ISituationListAndBuilder<RssDisplay>() {

					@Override
					public RssDisplay makeChild(Composite from) {
						IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(AllSoftwareFmDisplayTests.class, "Test");
						RssDisplay rssDisplay = new RssDisplay(from, SWT.BORDER, resourceGetter);
						return rssDisplay;
					}

					@Override
					public void selected(RssDisplay hasControl, String fileName, String value) throws Exception {
						hasControl.displayReply(200, value);
					}
				});
	}

}