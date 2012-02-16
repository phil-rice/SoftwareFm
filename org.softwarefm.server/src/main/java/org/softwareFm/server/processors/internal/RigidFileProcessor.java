/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.http.RequestLine;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.CommonMessages;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class RigidFileProcessor implements IProcessCall {

	private final Set<String> roots;
	private final File fileRoot;

	public RigidFileProcessor(File fileRoot, String... roots) {
		this.fileRoot = fileRoot;
		this.roots = Sets.makeSet(roots);
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		String method = requestLine.getMethod();
		if (CommonConstants.GET.equals(requestLine.getMethod()) || CommonConstants.HEAD.equals(method)) {
			String url = requestLine.getUri().substring(1);
			int index = url.indexOf('/');
			if (index != -1) {
				String root = url.substring(0, index);
				if (roots.contains(root)) {
					File file = new File(fileRoot, url);
					if (file.isFile())
						if (method.equals("HEAD"))
							return IProcessResult.Utils.processString("Found");
						else
							return IProcessResult.Utils.processFile(file);
					else
						return IProcessResult.Utils.processError(CommonConstants.notFoundStatusCode, CommonMessages.notFoundMessage);
				}
			}
		}
		return null;
	}

}