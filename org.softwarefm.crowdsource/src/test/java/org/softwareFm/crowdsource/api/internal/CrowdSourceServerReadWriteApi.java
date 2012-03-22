package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.ICrowdSourcedApi;

public class CrowdSourceServerReadWriteApi extends AbstractCrowdReadWriterApiTest {

	@Override
	protected ICrowdSourcedApi getApi() {
		return getServerApi();
	}
	
}
