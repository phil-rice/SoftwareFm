/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.internal;

import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;

public class LocalUserReader implements IUserReader {

	private final IUrlGenerator userGenerator;
	private final IGitLocal gitLocal;

	public LocalUserReader(IGitLocal gitLocal, IUrlGenerator userGenerator) {
		this.gitLocal = gitLocal;
		this.userGenerator = userGenerator;

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUserProperty(String softwareFmId, String userCrypto, String property) {
		Map<String, Object> data = getUserData(softwareFmId, userCrypto);

		if (data == null)
			throw new NullPointerException(softwareFmId + ", " + userCrypto + "," + property);
		return (T) data.get(property);
	}

	protected Map<String, Object> getUserData(String softwareFmId, String cryptoKey) {
		String url = userGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, cryptoKey);
		Map<String, Object> data = gitLocal.getFile(fileDescription);
		return data;
	}

	@Override
	public void refresh(String softwareFmId) {
		String url = userGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		gitLocal.clearCache(url);
	}

}