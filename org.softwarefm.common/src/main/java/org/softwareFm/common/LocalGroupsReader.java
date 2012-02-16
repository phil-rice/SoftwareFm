/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common;

import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;

public class LocalGroupsReader extends AbstractGroupReader {

	protected final IGitLocal gitLocal;

	public LocalGroupsReader(IUrlGenerator groupUrlGenerator, IGitLocal gitLocal) {
		super(groupUrlGenerator);
		this.gitLocal = gitLocal;
	}

	@Override
	protected String findUrl(String groupId) {
		String url = groupUrlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
		return url;
	}

	@Override
	protected String getFileAsString(IFileDescription groupFileDescription) {
		return gitLocal.getFileAsString(groupFileDescription);
	}

	@Override
	public void refresh(String groupId) {
		gitLocal.clearCache(findUrl(groupId));
	}
}