package org.softwarefm.httpServer.routes;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.utilities.http.HttpMethod;

public interface IRouteHandler {

	StatusAndEntity execute(HttpMethod method, Map<String, String> parameters, HttpEntity entity) throws Exception;

}
