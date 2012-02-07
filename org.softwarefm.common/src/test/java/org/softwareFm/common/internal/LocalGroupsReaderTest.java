package org.softwareFm.common.internal;

import java.io.File;

import org.softwareFm.common.LocalGroupsReader;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;

public class LocalGroupsReaderTest extends GitTest {

	private LocalGroupsReader groupsReader;
	private File localGroupFile1;
	private File remoteGroupFile1;
	private String group1Url;
	private String groupId1;
	private String groupCryptoKey;

	public void testGetGroupProperty() {
		remoteGroupFile1.getParentFile().mkdirs();
		Files.setText(remoteGroupFile1, Crypto.aesEncrypt(groupCryptoKey, Json.toString(v12)));
		remoteOperations.init(group1Url);
		remoteOperations.addAllAndCommit(group1Url, "for tests");
		assertEquals(1l, groupsReader.getGroupProperty(groupId1, groupCryptoKey, "c"));
		assertEquals(2l, groupsReader.getGroupProperty(groupId1, groupCryptoKey, "v"));

		assertEquals(v12, Json.mapFromEncryptedFile(localGroupFile1, groupCryptoKey));// only doable because no users yet
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		groupCryptoKey = Crypto.makeKey();
		IUrlGenerator groupGenerator = IUrlGenerator.Utils.generator("group/{0}/{1}/{2}", GroupConstants.groupIdKey);
		groupsReader = new LocalGroupsReader(groupGenerator, gitLocal);
		groupId1 = "groupId1";
		group1Url = groupGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1));
		remoteGroupFile1 = new File(remoteRoot, Urls.compose(group1Url, CommonConstants.dataFileName));
		localGroupFile1 = new File(localRoot, Urls.compose(group1Url, CommonConstants.dataFileName));
	}

}
