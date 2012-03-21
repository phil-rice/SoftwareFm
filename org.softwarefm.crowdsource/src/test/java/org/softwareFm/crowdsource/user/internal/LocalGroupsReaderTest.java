/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.user.internal;

import java.io.File;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class LocalGroupsReaderTest extends ApiTest {

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
		getServerApi().getServer();
		groupCryptoKey = Crypto.makeKey();
		IUrlGenerator groupGenerator = getServerConfig().groupUrlGenerator;
		groupsReader = new LocalGroupsReader(getLocalApi().makeReader(), groupGenerator);
		groupId1 = "groupId1";
		group1Url = groupGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId1));
		remoteGroupFile1 = new File(remoteRoot, Urls.compose(group1Url, CommonConstants.dataFileName));
		localGroupFile1 = new File(localRoot, Urls.compose(group1Url, CommonConstants.dataFileName));
	}

}