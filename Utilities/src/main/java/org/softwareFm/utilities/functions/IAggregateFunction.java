package org.softwareFm.utilities.functions;

public interface IAggregateFunction<T> {

	T apply(T left, T right);

}
