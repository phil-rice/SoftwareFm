package org.softwareFm.common.server.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.common.IUser;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.server.ICrowdSourcedServer;

public class LocalUserReaderTest extends GitTest {

	private IUser remoteUser;
	private IUserReader localUser;
	private final String crypto1Key = Crypto.makeKey();
	private final String crypto2Key = Crypto.makeKey();
	private final String sfmId1 = "sfmId1";
	private final String sfmId2 =  "sfmId2";
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
		remoteUser = ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, userUrlGenerator, findRepositoryRoot);
		localUser = IUserReader.Utils.localUserReader(gitLocal, userUrlGenerator);

		remoteSfmId1File = new File(remoteRoot, Urls.compose("user/sf/mI/sfmId1/", CommonConstants.dataFileName));
		remoteSfmId2File = new File(remoteRoot, Urls.compose("user/sf/mI/sfmId2/", CommonConstants.dataFileName));

		localSfmId1File = new File(localRoot, Urls.compose("user/sf/mI/sfmId1/", CommonConstants.dataFileName));
		localSfmId2File = new File(localRoot, Urls.compose("user/sf/mI/sfmId2/", CommonConstants.dataFileName));
	}


}
