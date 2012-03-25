/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.git.IGitWriter;
import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.CommonMessages;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.maps.UrlCache;

public class MakeRootProcessor extends AbstractCallProcessor {

	private final UrlCache<String> urlCache;
	private final IContainer container;

	public MakeRootProcessor(UrlCache<String> urlCache, IContainer container) {
		super(CommonConstants.POST, CommonConstants.makeRootPrefix);
		this.urlCache = urlCache;
		this.container = container;
	}

	@Override
	protected IProcessResult execute(final String actualUrl, Map<String, Object> parameters) {
		return container.access(IGitReader.class, IGitWriter.class, new IFunction2<IGitReader, IGitWriter, IProcessResult>() {
			@Override
			public IProcessResult apply(IGitReader reader, IGitWriter writer) throws Exception {
				IFileDescription fileDescription = IFileDescription.Utils.plain(actualUrl);
				File existing = fileDescription.findRepositoryUrl(reader.getRoot());
				if (existing == null) {
					writer.init(actualUrl, MakeRootProcessor.class.getSimpleName() + ": " + actualUrl);
					urlCache.clear(actualUrl);
					return IProcessResult.Utils.processString(MessageFormat.format(CommonMessages.madeRoot, actualUrl));
				} else {
					if (existing.equals(fileDescription.getDirectory(reader.getRoot())))
						return IProcessResult.Utils.processString(MessageFormat.format(CommonMessages.rootAlreadyExists, actualUrl));
					else
						return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, MessageFormat.format(CommonMessages.cannotCreateGitUnderSecondRepository, actualUrl));
				}
			}
		});
	}
}