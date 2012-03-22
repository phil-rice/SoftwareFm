package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;

public interface ICrowdSourcedReaderApi {
	@SuppressWarnings("Need to migrate away from this")
	IGitOperations gitOperations();
	<T> T accessGitReader(IFunction1<IGitReader, T> function);

	<T> T accessCommentsReader(IFunction1<ICommentsReader, T> function);

	<T> T accessGroupReader(IFunction1<IGroupsReader, T> function);

	<T> T accessUserReader(IFunction1<IUserReader, T> function);

	<T> T accessUserMembershipReader(IFunction2<IGroupsReader, IUserMembershipReader, T> function);

	<Result, API> Result access(Class<API> clazz, IFunction1<API, Result> function);

	<Result, A1, A2> Result access(Class<A1> clazz1, Class<A2> clazz2, IFunction2<A1, A2, Result> function);

	<Result, A1, A2, A3> Result access(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, IFunction3<A1, A2, A3, Result> function);
}
