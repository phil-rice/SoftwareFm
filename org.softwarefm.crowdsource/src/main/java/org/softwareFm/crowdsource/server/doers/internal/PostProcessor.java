/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.doers.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.json.Json;

public class PostProcessor implements ICallProcessor {

	private final IGitOperations gitOperations;

	public PostProcessor(IGitOperations gitOperations) {
		this.gitOperations = gitOperations;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(CommonConstants.POST)) {
			if (!parameters.containsKey(CommonConstants.dataParameterName))
				throw new IllegalArgumentException(requestLine + ", " + parameters);
			Object data = parameters.get(CommonConstants.dataParameterName);
			if (data instanceof String) {
				Map<String, Object> actualData = Json.mapFromString((String) parameters.get(CommonConstants.dataParameterName));
				IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, parameters);
				IFileDescription.Utils.merge(gitOperations, fileDescription, actualData);
			} else
				throw new IllegalArgumentException(requestLine + ", " + parameters);
			return IProcessResult.Utils.doNothing();
		}
		return null;
	}

}