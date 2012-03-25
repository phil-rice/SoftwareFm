/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.doers.internal;

import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitWriter;
import org.softwareFm.crowdsource.api.server.ICallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class DeleteProcessor implements ICallProcessor {

	private final IContainer container;

	public DeleteProcessor(IContainer container) {
		this.container = container;
	}

	@Override
	public IProcessResult process(final RequestLine requestLine, final Map<String, Object> parameters) {
		return container.access(IGitWriter.class, new IFunction1<IGitWriter, IProcessResult>() {
			@Override
			public IProcessResult apply(IGitWriter writer) throws Exception {
				if (requestLine.getMethod().equals(CommonConstants.DELETE)) {
					IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, parameters);
					String commitMessage = (String) Maps.getOrDefault(parameters, CommonConstants.commitMessageKey, DeleteProcessor.this.getClass() + ": " + requestLine.getUri());
						writer.delete(fileDescription, commitMessage);
					return IProcessResult.Utils.doNothing();
				}
				return null;
			}
		});
	}
}