package org.softwareFm.collections.mySoftwareFm;

public interface IRequestSaltCallback {

	void saltReceived(String salt);

	void problemGettingSalt(String message);

}
