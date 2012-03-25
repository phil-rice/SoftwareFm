/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.user.internal;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;

public class LocalGroupsReader extends AbstractGroupReader {

	public LocalGroupsReader(IContainer container, IUrlGenerator groupUrlGenerator) {
		super(container, groupUrlGenerator);
	}

	@Override
	protected String findUrl(String groupId) {
		String url = groupUrlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
		return url;
	}

	@Override
	public void refresh(final String groupId) {
		container.accessGitReader(new IFunction1<IGitReader, Void>() {
			@Override
			public Void apply(IGitReader gitReader) throws Exception {
				gitReader.clearCaches();
				return null;
			}
		});
	}
}