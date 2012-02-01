package org.softwareFm.server.processors.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.server.IFileDescription;
import org.softwareFm.server.IGitOperations;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.CommonMessages;
import org.softwareFm.server.processors.IProcessResult;
import org.softwareFm.utilities.maps.UrlCache;

public class MakeRootProcessor extends AbstractCommandProcessor {

	private final UrlCache<String> urlCache;

	public MakeRootProcessor(UrlCache<String> urlCache,IGitOperations gitOperations) {
		super(gitOperations, CommonConstants.POST, CommonConstants.makeRootPrefix);
		this.urlCache = urlCache;
	}

	@Override
	protected IProcessResult execute(String actualUrl, Map<String, Object> parameters) {
		IFileDescription fileDescription = IFileDescription.Utils.plain(actualUrl);
		File existing = fileDescription.findRepositoryUrl(gitOperations.getRoot());
		if (existing == null) {
			gitOperations.init(actualUrl);
			urlCache.clear(actualUrl);
			return IProcessResult.Utils.processString(MessageFormat.format(CommonMessages.madeRoot, actualUrl));
		}
		return null;
	}
}
