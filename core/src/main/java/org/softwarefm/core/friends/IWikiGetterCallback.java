package org.softwarefm.core.friends;

public interface IWikiGetterCallback <T>{

	/** If the browser isn't logged in... */
	void notLoggedIn();
	
	/** Some unpleasant response from the server */
	void badResponse(int statusCode, String textFromServer);
	
	/** Got the data */
	void success(T result);
	
	/** This is checked before the callbacks, and if false nothing is called */
	boolean isValid();
}
