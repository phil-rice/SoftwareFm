/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.server.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.git.GitTest;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserReader;
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

public class LocalUserReaderTest extends GitTest {

	private IUser remoteUser;
	private IUserReader localUser;
	private final String crypto1Key = Crypto.makeKey();
	private final String crypto2Key = Crypto.makeKey();
	private final String sfmId1 = "sfmId1";
	private final String sfmId2 = "sfmId2";
	private File remoteSfmId1File;
	private File remoteSfmId2File;
	private File localSfmId1File;
	private File localSfmId2File;
	protected IFunction1<String, String> findRepositoryRoot = Strings.firstNSegments(3);
	private final Map<String, Object> expected = Maps.stringObjectMap("someProperty", "someValue");

	public void testSet() {
		assertFalse(remoteSfmId1File.exists());
		assertFalse(remoteSfmId2File.exists());
		remoteUser.setUserProperty(sfmId1, crypto1Key, "someProperty", "someValue");
		remoteUser.setUserProperty(sfmId2, crypto2Key, "someProperty", "someValue");

		assertEquals(expected, Json.mapFromEncryptedFile(remoteSfmId1File, crypto1Key));
		assertEquals(expected, Json.mapFromEncryptedFile(remoteSfmId2File, crypto2Key));

		assertFalse(localSfmId1File.exists());
		assertFalse(localSfmId2File.exists());
	}

	public void testSetGet() {
		remoteUser.setUserProperty(sfmId1, crypto1Key, "someProperty", "someValue");
		remoteUser.setUserProperty(sfmId2, crypto2Key, "someProperty", "someValue");

		assertEquals("someValue", localUser.getUserProperty(sfmId1, crypto1Key, "someProperty"));
		assertEquals("someValue", localUser.getUserProperty(sfmId2, crypto2Key, "someProperty"));

		assertEquals(expected, Json.mapFromEncryptedFile(remoteSfmId1File, crypto1Key));
		assertEquals(expected, Json.mapFromEncryptedFile(remoteSfmId2File, crypto2Key));

		assertEquals(expected, Json.mapFromEncryptedFile(localSfmId1File, crypto1Key));
		assertEquals(expected, Json.mapFromEncryptedFile(localSfmId2File, crypto2Key));

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IUrlGenerator userUrlGenerator = IUrlGenerator.Utils.generator("user/{0}/{1}/{2}", LoginConstants.softwareFmIdKey);
		Map<String, Callable<Object>> defaults = Maps.<String, Callable<Object>> makeMap("defProperty", Callables.valueFromList("value"));
		remoteUser = ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, userUrlGenerator, findRepositoryRoot, defaults);
		localUser = IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);

		remoteSfmId1File = new File(remoteRoot, Urls.compose("user/sf/mI/sfmId1/", CommonConstants.dataFileName));
		remoteSfmId2File = new File(remoteRoot, Urls.compose("user/sf/mI/sfmId2/", CommonConstants.dataFileName));

		localSfmId1File = new File(localRoot, Urls.compose("user/sf/mI/sfmId1/", CommonConstants.dataFileName));
		localSfmId2File = new File(localRoot, Urls.compose("user/sf/mI/sfmId2/", CommonConstants.dataFileName));
	}

}