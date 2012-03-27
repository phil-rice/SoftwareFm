package org.softwareFm.crowdsource.utilities.services;

import java.util.concurrent.TimeUnit;

public interface IShutdown {
	void shutdown();
	void shutdownAndAwaitTermination(long timeout, TimeUnit unit);

}
