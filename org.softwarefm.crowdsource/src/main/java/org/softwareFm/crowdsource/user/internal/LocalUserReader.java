/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.user.internal;

import java.util.Map;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;

public class LocalUserReader implements IUserReader {

	private final IUrlGenerator userGenerator;
	private final IContainer container;

	public LocalUserReader(IContainer container, IUrlGenerator userGenerator) {
		this.container = container;
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

	protected Map<String, Object> getUserData(final String softwareFmId, final String cryptoKey) {
		return container.accessGitReader(new IFunction1<IGitReader, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(IGitReader from) throws Exception {
				String url = userGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
				IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, cryptoKey);
				Map<String, Object> data = from.getFile(fileDescription);
				return data;
			}
		}).get(container.defaultTimeOutMs());
	}

	@Override
	public void refresh(String softwareFmId) {
		IGitReader.Utils.clearCache(container);
	}
}