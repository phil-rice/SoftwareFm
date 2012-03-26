package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;

public interface ICrowdSourcedReaderApi {
	
	<T> T accessGitReader(IFunction1<IGitReader, T> function);

	<T> T accessCommentsReader(IFunction1<ICommentsReader, T> function);


	<Result, API> Result access(Class<API> clazz, IFunction1<API, Result> function);

	<Result, A1, A2> Result access(Class<A1> clazz1, Class<A2> clazz2, IFunction2<A1, A2, Result> function);

	<Result, A1, A2, A3> Result access(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, IFunction3<A1, A2, A3, Result> function);
}
