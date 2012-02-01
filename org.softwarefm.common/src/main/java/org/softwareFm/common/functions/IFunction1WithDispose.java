package org.softwareFm.common.functions;

public interface IFunction1WithDispose<From, To> extends IFunction1<From, To> {

	void dispose();

}
