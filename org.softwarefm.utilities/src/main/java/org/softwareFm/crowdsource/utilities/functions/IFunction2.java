package org.softwareFm.crowdsource.utilities.functions;

public interface IFunction2<From1, From2, To> {
	To apply(From1 from1, From2 from2) throws Exception;
}
