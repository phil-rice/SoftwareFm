/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.user;

import java.util.Map;

import junit.framework.Assert;

import org.softwareFm.common.IUser;
import org.softwareFm.common.maps.Maps;

public class UserMock implements IUser {

	private Map<String, Object> map = Maps.newMap();
	private final String expectedUserDetails;
	private final String expectedCrypto;

	public UserMock(String expectedCrypto, String softwareFmId, Object... namesAndValues) {
		this.map = Maps.stringObjectMap(namesAndValues);
		this.expectedCrypto = expectedCrypto;
		this.expectedUserDetails = softwareFmId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getUserProperty(String softwareFmId, String userCrypto, String property) {
		Assert.assertEquals(expectedUserDetails, softwareFmId);
		Assert.assertEquals(expectedCrypto, userCrypto);
		return (T) map.get(property);
	}

	@Override
	public <T> void setUserProperty(String softwareFmId, String cryptoKey, String property, T value) {
		Assert.assertEquals(expectedUserDetails, softwareFmId);
		Assert.assertEquals(expectedCrypto, cryptoKey);
		map.put(property, value);
	}

	@Override
	public void refresh(String softwareFmId) {
	}
}