package org.softwareFm.crowdsource.navigation;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.server.IServerDoer;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

public interface IRepoNavigation extends IServerDoer {

	ITransaction<Map<String, List<String>>> navigationData(String url, ICallback<Map<String, List<String>>> callback);
}
