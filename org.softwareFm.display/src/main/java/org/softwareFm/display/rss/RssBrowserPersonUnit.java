package org.softwareFm.display.rss;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.display.swt.ISituationListAndBuilder;
import org.softwareFm.display.swt.Swts;

public class RssBrowserPersonUnit {
	public static void main(String[] args) {
		final File root = new File("../org.softwareFm.display/src/test/resources/org/softwareFm/display/rss");
		Swts.xUnit("Rss Person Unit", root, "xml", //
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
