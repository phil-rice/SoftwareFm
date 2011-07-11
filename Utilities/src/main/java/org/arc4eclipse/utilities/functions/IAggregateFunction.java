package org.arc4eclipse.utilities.functions;

public interface IAggregateFunction<T> {

	T apply(T left, T right);

}
