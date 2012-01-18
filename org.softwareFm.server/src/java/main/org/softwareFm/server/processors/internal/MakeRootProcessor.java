package org.softwareFm.server.processors.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.maps.UrlCache;

public class MakeRootProcessor extends AbstractCommandProcessor {

	private final UrlCache<String> urlCache;

	public MakeRootProcessor(UrlCache<String> urlCache, IGitServer server) {
		super(server, ServerConstants.POST, ServerConstants.makeRootPrefix);
		this.urlCache = urlCache;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		File existing = server.findRepositoryUrl(actualUrl);
		if (existing == null) {
			server.createRepository(actualUrl);
			urlCache.clear(actualUrl);
			return IProcessResult.Utils.processString(MessageFormat.format(ServerConstants.madeRoot, actualUrl));
		}
		return null;
	}
}
