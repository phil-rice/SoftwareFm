package org.softwareFm.display;

import java.util.Map;

public interface IUrlDataCallback {

	void processData(String entity, String url, Map<String, Object> context, Map<String, Object> data);

}
