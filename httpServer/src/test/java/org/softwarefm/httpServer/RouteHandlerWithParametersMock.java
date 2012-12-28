package org.softwarefm.httpServer;

import org.softwarefm.httpServer.routes.IRouteHandlerWithParameters;
import org.softwarefm.httpServer.routes.RouteHandlerMock;

public class RouteHandlerWithParametersMock  extends RouteHandlerMock implements IRouteHandlerWithParameters{

	public RouteHandlerWithParametersMock(String responseString, int statusCode) {
		super(responseString, statusCode);
	}

}
