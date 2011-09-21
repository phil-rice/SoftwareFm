package org.softwareFm.eclipse;

public interface IStoreUpdatedCallback {

	void storeUpdates(String url, String entity, String attribute, Object newValue);

}
