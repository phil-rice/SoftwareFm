package org.softwareFm.crowdsource.utilities.functions;

public interface IFunction3<From1, From2, From3, To> {
	To apply(From1 from1, From2 from2, From3 from3) throws Exception;
}
