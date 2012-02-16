/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.http.RequestLine;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class GetFileProcessor implements IProcessCall {
	private final File root;
	private final Set<String> knownExtensions;

	public GetFileProcessor(File root, String... knownExtensions) {
		this.root = root;
		this.knownExtensions = Sets.makeSet(knownExtensions);
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (CommonConstants.GET.equals(requestLine.getMethod())) {
			String url = requestLine.getUri();
			String extension = Files.extension(url);
			if (knownExtensions.contains(extension)) {
				File file = new File(root, url);
				if (file.isFile())
					return IProcessResult.Utils.processFile(file);
			}
		}
		return null;
	}

}