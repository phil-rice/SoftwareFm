package org.softwareFm.server.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.server.GitTest;
import org.softwareFm.server.IUser;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.json.Json;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;
import org.softwareFm.utilities.url.IUrlGenerator;
import org.softwareFm.utilities.url.Urls;

public class ServerUserTest extends GitTest {

	private IUser user;
	private final String crypto1Key = Crypto.makeKey();
	private final String crypto2Key = Crypto.makeKey();
	private final Map<String, Object> sfmId1 = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "sfmId1");
	private final Map<String, Object> sfmId2 = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, "sfmId2");
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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IUrlGenerator userUrlGenerator = IUrlGenerator.Utils.generator("user/{0}/{1}/{2}", LoginConstants.softwareFmIdKey);
		user = IUser.Utils.makeUserForServer(remoteOperations, userUrlGenerator, findRepositoryRoot);
		sfmId1File = new File(remoteRoot, Urls.compose("user/sf/mI/sfmId1/", CommonConstants.dataFileName));
		sfmId2File = new File(remoteRoot, Urls.compose("user/sf/mI/sfmId2/", CommonConstants.dataFileName));
	}

}
