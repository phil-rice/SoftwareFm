package org.softwareFm.explorer.eclipse;

import java.io.File;

import org.softwareFm.display.browser.IBrowserCompositeBuilder;

public interface IExplorer extends IBrowserCompositeBuilder{

	void displayCard(String url);

	void displayUnrecognisedJar(File file, String digest );

	void displayComments(String url);


}
