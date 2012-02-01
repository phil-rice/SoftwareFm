package org.softwareFm.swt.mySoftwareFm;

public interface IRequestSaltCallback {

	void saltReceived(String salt);

	void problemGettingSalt(String message);

}
