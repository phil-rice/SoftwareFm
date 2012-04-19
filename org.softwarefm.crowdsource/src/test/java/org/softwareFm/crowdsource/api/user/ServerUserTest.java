/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.user;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.git.GitTest;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class ServerUserTest extends GitTest {

	private IUser user;
	private final String crypto1Key = Crypto.makeKey();
	private final String crypto2Key = Crypto.makeKey();
	private final String sfmId1 = "sfmId1";
	private final String sfmId2 = "sfmId2";
	private File sfmId1File;
	private File sfmId2File;
	protected IFunction1<String, String> findRepositoryRoot = Strings.firstNSegments(3);

	public void testSet() {
		assertFalse(sfmId1File.exists());
		assertFalse(sfmId2File.exists());
		user.setUserProperty(sfmId1, crypto1Key, "someProperty", "someValue");
		user.setUserProperty(sfmId2, crypto2Key, "someProperty", "someValue");

		Map<String, Object> expected = Maps.stringObjectMap("someProperty", "someValue");
		assertEquals(expected, Json.mapFromEncryptedFile(sfmId1File, crypto1Key));
		assertEquals(expected, Json.mapFromEncryptedFile(sfmId2File, crypto2Key));
	}

	public void testSetWhenFileExists() {
		user.setUserProperty(sfmId1, crypto1Key, "someProperty1", "someValue1");
		user.setUserProperty(sfmId1, crypto1Key, "someProperty2", "someValue2");
		assertEquals("someValue1", user.getUserProperty(sfmId1, crypto1Key, "someProperty1"));
		assertEquals("someValue2", user.getUserProperty(sfmId1, crypto1Key, "someProperty2"));

	}

	public void testSetGet() {
		user.setUserProperty(sfmId1, crypto1Key, "someProperty", "someValue");
		user.setUserProperty(sfmId2, crypto2Key, "someProperty", "someValue");
		assertEquals("someValue", user.getUserProperty(sfmId1, crypto1Key, "someProperty"));
		assertEquals("someValue", user.getUserProperty(sfmId2, crypto2Key, "someProperty"));
	}

	public void testGetWithDefaultValue() {
		user.setUserProperty(sfmId1, crypto1Key, "someProperty", "someValue");

		assertEquals("defValue1", user.getUserProperty(sfmId1, crypto1Key, "def1"));
		assertEquals("defValue1", user.getUserProperty(sfmId1, crypto1Key, "def1"));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IUrlGenerator userUrlGenerator = IUrlGenerator.Utils.generator("user/{0}/{1}/{2}", LoginConstants.softwareFmIdKey);
		user = ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, userUrlGenerator, findRepositoryRoot, Maps.<String, Callable<Object>> makeMap("def1", Callables.valueFromList("defValue1")));
		sfmId1File = new File(remoteRoot, Urls.compose("user/sf/mI/sfmId1/", CommonConstants.dataFileName));
		sfmId2File = new File(remoteRoot, Urls.compose("user/sf/mI/sfmId2/", CommonConstants.dataFileName));
	}

}