package org.softwareFm.explorer.eclipse;

import java.io.File;

import org.softwareFm.card.api.ICardChangedListener;
import org.softwareFm.display.browser.IBrowserCompositeBuilder;
import org.softwareFm.display.timeline.ITimeLine;

public interface IExplorer extends IBrowserCompositeBuilder, ITimeLine{

	void displayCard(String url);

	void displayUnrecognisedJar(File file, String digest );

	void displayComments(String url);
	
	void addCardListener(ICardChangedListener listener);

}
