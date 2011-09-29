package org.softwareFm.display.rss;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.AllSoftwareFmDisplayTests;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class RssPersonUnit {
	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.display/src/test/resources/org/softwareFm/display/rss");
		Swts.xUnit("Rss Person Unit", root, "xml", //
				new IFunction1<Composite, RssDisplay>() {
					@Override
					public RssDisplay apply(final Composite from) throws Exception {
						IResourceGetter resourceGetter = IResourceGetter.Utils.noResources().with(AllSoftwareFmDisplayTests.class, "Test");
						RssDisplay rssDisplay = new RssDisplay(from, SWT.BORDER, resourceGetter);
						return rssDisplay;
					}
				}, new ISituationListCallback<RssDisplay>() {
					@Override
					public void selected(RssDisplay hasControl, String value) {
						String text = Files.getText(new File(root, value));
						hasControl.displayReply(200, text);
					}
				});
	}

}
