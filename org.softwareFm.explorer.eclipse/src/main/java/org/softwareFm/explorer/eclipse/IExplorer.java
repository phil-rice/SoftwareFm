package org.softwareFm.explorer.eclipse;

import org.softwareFm.display.browser.IBrowserCompositeBuilder;

public interface IExplorer extends IBrowserCompositeBuilder{

	void displayCard(String url);

	void displayUnrecognisedJar(String url, String file);

	void displayComments(String url);


}
