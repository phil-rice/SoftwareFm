/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.git.internal;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.ICrowdSourcedReaderApi;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.CommonMessages;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class GitLocalTest extends ApiTest {
	private ICrowdSourcedReaderApi localReaderApi;

	public void testGetFileWhenNeedToCreate() {
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b"), v11);
		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b/d"), v21);
	}

	public void testGetFile() {
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a"), v11);
		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b"), v11);
		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b/d"), v21);
	}

	public void testGetFileAsStringWhenNoLocalRepository() {
		String crypto = Crypto.makeKey();
		IFileDescription ac = IFileDescription.Utils.encrypted("a/c", "file", crypto);

		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v12);
		remoteOperations.put(ac, v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		assertEquals(v11, Json.mapFromString(IGitReader.Utils.getFileAsString(localReaderApi, IFileDescription.Utils.plain("a"))));
		assertEquals(v12, Json.mapFromString(IGitReader.Utils.getFileAsString(localReaderApi, IFileDescription.Utils.plain("a/b"))));
		assertEquals(v21, Json.mapFromString(Crypto.aesDecrypt(crypto, IGitReader.Utils.getFileAsString(localReaderApi, ac))));
	}

	public void testGetFileAsStringWhenLocalRepository() {
		String crypto = Crypto.makeKey();
		IFileDescription ac = IFileDescription.Utils.encrypted("a/c", "file", crypto);

		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v12);
		remoteOperations.put(ac, v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		assertEquals(v11, Json.mapFromString(IGitReader.Utils.getFileAsString(localReaderApi, IFileDescription.Utils.plain("a"))));
		assertEquals(v12, Json.mapFromString(IGitReader.Utils.getFileAsString(localReaderApi, IFileDescription.Utils.plain("a/b"))));
		assertEquals(v21, Json.mapFromString(Crypto.aesDecrypt(crypto, IGitReader.Utils.getFileAsString(localReaderApi, ac))));
	}

	@SuppressWarnings("unchecked")
	public void testGetFileAsListOfMapsWhenNoLocalRepositoryWithEncryptedFiles() {
		String crypto = Crypto.makeKey();
		IFileDescription ac = IFileDescription.Utils.encrypted("a/c", "file", crypto);

		remoteOperations.init("a");
		remoteOperations.append(ac, v11);
		remoteOperations.append(ac, v12);
		remoteOperations.append(ac, v21);
		remoteOperations.append(ac, v22);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		assertEquals(Arrays.asList(v11, v12, v21, v22), IGitReader.Utils.getFileAsListOfMaps(localReaderApi, ac));
	}

	@SuppressWarnings("unchecked")
	public void testGetFileAsMapsWhenOneLineBadlyEncrypted() {
		String crypto = Crypto.makeKey();
		String badCrypto = Crypto.makeKey();
		IFileDescription ac = IFileDescription.Utils.encrypted("a/c", "file", crypto);
		IFileDescription acBad = IFileDescription.Utils.encrypted("a/c", "file", badCrypto);

		remoteOperations.init("a");
		remoteOperations.append(ac, v11);
		remoteOperations.append(ac, v12);
		remoteOperations.append(acBad, v21);
		remoteOperations.append(ac, v22);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		Map<String, Object> bad = Maps.stringObjectMap(CommonConstants.errorKey, MessageFormat.format(CommonMessages.cannotDecrypt, Crypto.aesEncrypt(badCrypto, Json.toString(v21))));
		assertEquals(Arrays.asList(v11, v12, bad, v22), IGitReader.Utils.getFileAsListOfMaps(localReaderApi,ac));

	}

	@SuppressWarnings("unchecked")
	public void testGetFileAsListOfMapsWhenNoLocalRepositoryWithPlainFiles() {
		IFileDescription ac = IFileDescription.Utils.plain("a/c");

		remoteOperations.init("a");
		remoteOperations.append(ac, v11);
		remoteOperations.append(ac, v12);
		remoteOperations.append(ac, v21);
		remoteOperations.append(ac, v22);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		assertEquals(Arrays.asList(v11, v12, v21, v22), IGitReader.Utils.getFileAsListOfMaps(localReaderApi,ac));
	}

	@SuppressWarnings("unchecked")
	public void testGetFileAsListOfMapsWhenLocalRepository() {
		IFileDescription ac = IFileDescription.Utils.plain("a/c");

		remoteOperations.init("a");
		remoteOperations.append(ac, v11);
		remoteOperations.append(ac, v12);
		remoteOperations.append(ac, v21);
		remoteOperations.append(ac, v22);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		assertEquals(Arrays.asList(v11, v12, v21, v22), IGitReader.Utils.getFileAsListOfMaps(localReaderApi,ac));
	}

	public void testGetFileAndDescendants() {
		Map<String, Object> map = Maps.with(v11, "c", v12, "d", v21);
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		checkGetFileAndDescendants(localReaderApi, IFileDescription.Utils.plain("a/b"), map);
		checkGetFileAndDescendants(localReaderApi, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFileAndDescendants(localReaderApi, IFileDescription.Utils.plain("a/b/d"), v21);
	}

	public void testGetFileAboveRepo() {
		remoteOperations.init("a/b/c");
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		assertEquals(Maps.stringObjectMap("b", Maps.stringObjectMap("c", v12)), IGitReader.Utils.getFileAsMap(localReaderApi, IFileDescription.Utils.plain("a")));
	}

	public void testGetFileAndDescendantsAboveRepo() {
		remoteOperations.init("a/b/c");
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFileAndDescendants(localReaderApi, IFileDescription.Utils.plain("a"), Maps.stringObjectMap("b", Maps.stringObjectMap("c", v12)));
	}

	public void testClearCache() {
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b"), v11);
		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b/d"), v21);

		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v22);
		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b"), v11);// timeout hasn't happened, so pull doesn't actually take place
		IGitReader.Utils.clearCache(localReaderApi);
		checkGetFile(localReaderApi, IFileDescription.Utils.plain("a/b"), v22);// timeout has happened
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getServerApi().getServer();
		localReaderApi = getLocalApi().makeReader();

	}
}