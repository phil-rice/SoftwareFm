package org.softwareFm.display.browser;

import org.softwareFm.utilities.functions.IFunction1;


public interface IBrowserServiceBuilder extends IBrowserService{
	/** This renders the result of calling the url. For example if the url returns html, the identity function could be used. If the url returns and rss feed, then the XML has to be turned into html 
	 * @return */
	IBrowserService register(String feedType, IFunction1<String, String> feedPostProcessor);

}
