package org.softwareFm.crowdsource.comments.internal;

import org.softwareFm.crowdsource.api.ICrowdSourcesApi;

public class CommentsReaderLocalTest extends AbstractCommentsReaderTest {

	@Override
	protected ICrowdSourcesApi getApi() {
		getServerApi().getServer();
		return getLocalApi();
	}

}
