/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.server.internal;

import java.util.Arrays;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.server.GitTest;

public class GitLocalTest extends GitTest {
	public void testGetFileWhenNeedToCreate() {
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b"), v11);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/d"), v21);
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

		checkGetFile(gitLocal, IFileDescription.Utils.plain("a"), v11);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b"), v11);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/d"), v21);
	}

	public void testGetFileAsStringWhenNoLocalRepository() {
		String crypto = Crypto.makeKey();
		IFileDescription ac = IFileDescription.Utils.encrypted("a/c", "file", crypto);

		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v12);
		remoteOperations.put(ac, v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		assertEquals(v11, Json.mapFromString(gitLocal.getFileAsString(IFileDescription.Utils.plain("a"))));
		assertEquals(v12, Json.mapFromString(gitLocal.getFileAsString(IFileDescription.Utils.plain("a/b"))));
		assertEquals(v21, Json.mapFromString(Crypto.aesDecrypt(crypto, gitLocal.getFileAsString(ac))));

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

		assertEquals(v11, Json.mapFromString(gitLocal.getFileAsString(IFileDescription.Utils.plain("a"))));
		assertEquals(v12, Json.mapFromString(gitLocal.getFileAsString(IFileDescription.Utils.plain("a/b"))));
		assertEquals(v21, Json.mapFromString(Crypto.aesDecrypt(crypto, gitLocal.getFileAsString(ac))));
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

		assertEquals(Arrays.asList(v11, v12, v21, v22), gitLocal.getFileAsListOfMaps(ac));
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

		assertEquals(Arrays.asList(v11, v12, v21, v22), gitLocal.getFileAsListOfMaps(ac));
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

		assertEquals(Arrays.asList(v11, v12, v21, v22), gitLocal.getFileAsListOfMaps(ac));
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

		checkGetFileAndDescendants(gitLocal, IFileDescription.Utils.plain("a/b"), map);
		checkGetFileAndDescendants(gitLocal, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFileAndDescendants(gitLocal, IFileDescription.Utils.plain("a/b/d"), v21);
	}

	public void testGetFileAboveRepo() {
		remoteOperations.init("a/b/c");
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		assertEquals(Maps.stringObjectMap("b", Maps.stringObjectMap("c", v12)), gitLocal.getFile(IFileDescription.Utils.plain("a")));
	}

	public void testGetFileAndDescendantsAboveRepo() {
		remoteOperations.init("a/b/c");
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFileAndDescendants(gitLocal, IFileDescription.Utils.plain("a"), Maps.stringObjectMap("b", Maps.stringObjectMap("c", v12)));
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

		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b"), v11);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/c"), v12);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b/d"), v21);

		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v22);
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b"), v11);// timeout hasn't happened, so pull doesn't actually take place
		gitLocal.clearCaches();
		checkGetFile(gitLocal, IFileDescription.Utils.plain("a/b"), v22);// timeout has happened
	}
}