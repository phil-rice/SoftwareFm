package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;

public interface IContainer {

	int activeJobs();

	
	ITransactionManager getTransactionManager();
	
	ITransaction<Void> accessComments(ICallback<IComments> callback);

	<T> ITransaction<T> accessGitReader(IFunction1<IGitReader, T> function, ICallback<T> resultCallback);

	<T> ITransaction<T> accessCommentsReader(IFunction1<ICommentsReader, T> function, ICallback<T> resultCallback);

	<Result, API> ITransaction<Result> access(Class<API> clazz, IFunction1<API, Result> function);

	<Result, API> ITransaction<Result> accessWithCallback(Class<API> clazz, IFunction1<API, Result> function, ICallback<Result> resultCallback);

	<Result, Intermediate, API> ITransaction<Result> accessWithCallbackFn(Class<API> clazz, IFunction1<API, Intermediate> function, IFunction1<Intermediate, Result> resultCallback);

	<Result, A1, A2> ITransaction<Result> accessWithCallback(Class<A1> clazz1, Class<A2> clazz2, IFunction2<A1, A2, Result> function, ICallback<Result> resultCallback);

	<Result, Intermediate, A1, A2> ITransaction<Result> accessWithCallbackFn(Class<A1> clazz1, Class<A2> clazz2, IFunction2<A1, A2, Intermediate> function, IFunction1<Intermediate, Result> resultCallback);

	<Result, Intermediate, A1, A2, A3> ITransaction<Result> accessWithCallbackFn(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, IFunction3<A1, A2, A3, Intermediate> function, IFunction1<Intermediate, Result> resultCallback);

	<Result, A1, A2, A3> ITransaction<Result> accessWithCallback(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, IFunction3<A1, A2, A3, Result> function, ICallback<Result> resultCallback);

	<API> ITransaction<Void> access(Class<API> clazz, ICallback<API> callback);

	<A1, A2> ITransaction<Void> access(Class<A1> clazz1, Class<A2> clazz2, ICallback2<A1, A2> callback);

	<A1, A2, A3> ITransaction<Void> access(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, ICallback3<A1, A2, A3> callback);

	ITransactionManager transactionManager();

	IGitOperations gitOperations();

}
