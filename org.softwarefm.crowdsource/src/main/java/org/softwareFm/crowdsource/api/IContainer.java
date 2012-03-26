package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;

public interface IContainer extends ICrowdSourcedReaderApi {




	void modifyComments(ICallback<IComments> callback);

	<API> void modify(Class<API> clazz, ICallback<API> callback);

	<A1, A2> void modify(Class<A1> clazz1, Class<A2> clazz2, ICallback2<A1, A2> callback);

	<A1, A2, A3> void modify(Class<A1> clazz1, Class<A2> clazz2, Class<A3> clazz3, ICallback3<A1, A2, A3> callback);

	IGitOperations gitOperations();
}