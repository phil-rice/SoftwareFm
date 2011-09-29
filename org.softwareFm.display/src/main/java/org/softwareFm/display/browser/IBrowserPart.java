package org.softwareFm.display.browser;

import org.softwareFm.display.composites.IHasComposite;

public interface IBrowserPart extends IHasComposite{

	boolean usesUrl();
	
	void displayUrl(String url);
	
	void displayReply(int statusCode, String reply);

}
