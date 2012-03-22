package org.softwareFm.crowdsource.api;

public interface IApiBuilder extends ICrowdSourcedReadWriteApi {
	<T, X extends T> void registerReader(Class<T> class1, X x);

	<T, X extends T> void registerReadWriter(Class<T> class1, X x);

	<T, X extends T> void registerReaderAndWriter(Class<T> class1, X x);

}
