package org.softwareFm.common.server.internal;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.server.ICrowdSourcedServer;

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
	
	public void testGetWithDefaultValue(){
		user.setUserProperty(sfmId1, crypto1Key, "someProperty", "someValue");
		
		assertEquals("defValue1",  user.getUserProperty(sfmId1, crypto1Key, "def1"));
		assertEquals("defValue1",  user.getUserProperty(sfmId1, crypto1Key, "def1"));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IUrlGenerator userUrlGenerator = IUrlGenerator.Utils.generator("user/{0}/{1}/{2}", LoginConstants.softwareFmIdKey);
		user = ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, userUrlGenerator, findRepositoryRoot, Maps.<String,Callable<Object>>makeMap("def1", Callables.valueFromList("defValue1")));
		sfmId1File = new File(remoteRoot, Urls.compose("user/sf/mI/sfmId1/", CommonConstants.dataFileName));
		sfmId2File = new File(remoteRoot, Urls.compose("user/sf/mI/sfmId2/", CommonConstants.dataFileName));
	}

}
