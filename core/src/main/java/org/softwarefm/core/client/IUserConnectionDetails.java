package org.softwarefm.core.client;

import org.softwarefm.utilities.http.IHttpClient;

public interface IUserConnectionDetails {

	String getHost();
	
	String getUser();
	
	int getPort();
	
	IHttpClient getHttpClient();
}
