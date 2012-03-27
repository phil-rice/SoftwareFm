package org.softwareFm.crowdsource.utilities.transaction;

public interface ITransaction<T> {

	T get();

	T get(long millisecondsToWait);

	void cancel();

	boolean isCancelled();

	boolean isDone();
}
