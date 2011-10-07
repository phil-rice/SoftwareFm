package org.softwareFm.eclipse;

import java.util.Map;

public interface IStoreUpdatedCallback {

	void storeUpdates(String url, String entity, Map<String,Object> data);

}
