/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.jarAndClassPath.internal.AbstractUsageReader;

public class UsageReaderForServer extends AbstractUsageReader {

	public UsageReaderForServer(IContainer container, IUrlGenerator userUrlGenerator) {
		super(container, userUrlGenerator);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Map<String, Map<String, List<Integer>>> getProjectDetails(final IFileDescription projectFileDescription) {
		return container.accessGitReader(new IFunction1<IGitReader, Map<String, Map<String, List<Integer>>>>() {
			@Override
			public Map<String, Map<String, List<Integer>>> apply(IGitReader gitReader) throws Exception {
				Map<String, Map<String, List<Integer>>> projectDetails = (Map) gitReader.getFile(projectFileDescription);
				return projectDetails;
			}
		});
	}

}