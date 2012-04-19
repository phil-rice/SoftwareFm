/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.RequestLine;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.maps.UrlCache;

public class GitGetProcessor implements ICallProcessor {
	private final UrlCache<String> cache;
	private final IContainer container;

	public GitGetProcessor(IContainer container, UrlCache<String> cache) {
		this.container = container;
		this.cache = cache;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (CommonConstants.GET.equals(requestLine.getMethod())) {
			return IProcessResult.Utils.processString(getString(requestLine));
		}
		return null;
	}

	private String getString(final RequestLine requestLine) {
		final IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, Maps.emptyStringObjectMap());
		final String url = requestLine.getUri();
		String existing = cache.get(url);
		if (existing != null)
			return existing;
		String result = container.access(IGitReader.class, new IFunction1<IGitReader, String>() {
			@Override
			public String apply(final IGitReader gitReader) throws Exception {
				File root = gitReader.getRoot();
				File repositoryLocation = fileDescription.findRepositoryUrl(root);
				if (repositoryLocation == null) {
					return cache.findOrCreate(url, new Callable<String>() {
						@Override
						public String call() throws Exception {
							IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, Maps.emptyStringObjectMap());
							Map<String, Object> data = gitReader.getFileAndDescendants(fileDescription, 2);
							return Json.mapToString(CommonConstants.dataKey, data);
						}
					});
				} else {
					String repoUrl = Files.offset(root, repositoryLocation);
					return Json.toString(Maps.stringObjectLinkedMap(CommonConstants.repoUrlKey, repoUrl));
				}
			}
		}).get();
		return result;

	}

}