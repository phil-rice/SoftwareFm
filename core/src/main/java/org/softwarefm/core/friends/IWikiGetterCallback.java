package org.softwarefm.core.friends;

public interface IWikiGetterCallback <T>{

	/** If the browser isn't logged in... */
	void notLoggedIn();
	
	/** Got the data */
	void success(T result);
	
	/** This is checked before the callbacks, and if false nothing is called */
	boolean isValid();
}
