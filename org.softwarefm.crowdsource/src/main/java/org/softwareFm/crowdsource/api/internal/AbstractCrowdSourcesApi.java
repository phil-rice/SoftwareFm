package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.ICrowdSourcesApi;

abstract public class AbstractCrowdSourcesApi implements ICrowdSourcesApi {
	@Override
	public void shutdown() {
	}

	@Override
	public ICrowdSourcedServer getServer() {
		throw new UnsupportedOperationException();
	}



}
