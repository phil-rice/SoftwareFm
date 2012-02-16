/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.CommonMessages;
import org.softwareFm.common.maps.UrlCache;
import org.softwareFm.server.processors.IProcessResult;

public class MakeRootProcessor extends AbstractCommandProcessor {

	private final UrlCache<String> urlCache;

	public MakeRootProcessor(UrlCache<String> urlCache, IGitOperations gitOperations) {
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
		} else {
			if (existing.equals(fileDescription.getDirectory(gitOperations.getRoot())))
				return IProcessResult.Utils.processString(MessageFormat.format(CommonMessages.rootAlreadyExists, actualUrl));
			else
				return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, MessageFormat.format(CommonMessages.cannotCreateGitUnderSecondRepository, actualUrl));
		}
	}
}