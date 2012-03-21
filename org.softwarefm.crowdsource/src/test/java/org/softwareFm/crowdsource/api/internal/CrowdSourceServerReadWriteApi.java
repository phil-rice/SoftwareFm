package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.ICrowdSourcesApi;

public class CrowdSourceServerReadWriteApi extends AbstractCrowdReadWriterApiTest {

	@Override
	protected ICrowdSourcesApi getApi() {
		return getServerApi();
	}
	
}
