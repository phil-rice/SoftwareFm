package org.softwareFm.crowdsource.utilities.transaction;

public interface ITransaction<T> {

	/** waits for the default time out. */
	T get();

	T get(long millisecondsToWait);

	void cancel();

	boolean isCancelled();

	boolean isDone();
}
