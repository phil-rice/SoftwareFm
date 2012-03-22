package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;

abstract public class AbstractCrowdSourcesApi implements ICrowdSourcedApi {
	@Override
	public void shutdown() {
	}

	@Override
	public ICrowdSourcedServer getServer() {
		throw new UnsupportedOperationException();
	}



}
