package org.softwareFm.crowdsource.comments.internal;

import org.softwareFm.crowdsource.api.ICrowdSourcedApi;

public class CommentsReaderLocalTest extends AbstractCommentsReaderTest {

	@Override
	protected ICrowdSourcedApi getApi() {
		getServerApi().getServer();
		return getLocalApi();
	}

}
