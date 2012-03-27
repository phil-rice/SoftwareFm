/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.jarAndClassPath.api.ISoftwareFmApiFactory.Utils;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;

public class SoftwareFmServerTest extends ApiTest {

	private String softwareFmId;

	public void testCreatesMembershipAndGroupCrypto() {
		checkPropertyIsCreatedAndIsCrypto(GroupConstants.membershipCryptoKey);
		checkPropertyIsCreatedAndIsCrypto(GroupConstants.groupCryptoKey);
		checkPropertyIsCreatedAndIsCrypto(JarAndPathConstants.projectCryptoKey);
	}

	protected void checkPropertyIsCreatedAndIsCrypto(final String key) {
		getServerUserAndGroupsContainer().accessUserReader(new IFunction1<IUserReader, Void>() {
			@Override
			public Void apply(IUserReader user) throws Exception {
				String crypto1 = user.getUserProperty(softwareFmId, userKey0, key);
				String crypto2 = user.getUserProperty(softwareFmId, userKey0, key);
				assertNotNull(crypto1);
				assertEquals(crypto1, crypto2);

				String encrypted = Crypto.aesEncrypt(crypto1, "value");
				assertEquals("value", Crypto.aesDecrypt(crypto1, encrypted));
				return null;
			}
		}, ICallback.Utils.<Void>noCallback()).get();
	}
	
	@Override
	protected Map<String, Callable<Object>> getDefaultUserValues() {
		return Utils.getDefaultUserValues();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		truncateUsersTable();
		softwareFmId = createUser();
	}
}