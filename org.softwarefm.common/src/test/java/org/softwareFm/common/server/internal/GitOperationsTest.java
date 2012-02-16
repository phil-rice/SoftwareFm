/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.server.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.server.GitTest;
import org.softwareFm.common.url.Urls;

public class GitOperationsTest extends GitTest {

	public void testInit() {
		for (int i = 0; i < 10; i++) {
			localOperations.init("a");
			localOperations.init("b/c");
		}
		assertTrue(new File(localRoot, Urls.compose("a", CommonConstants.DOT_GIT)).exists());
		assertTrue(new File(localRoot, Urls.compose("b", "c", CommonConstants.DOT_GIT)).exists());
		checkRepositoryExists(localRoot, "a");
		checkRepositoryExists(localRoot, "b/c");
	}

	public void getGetFileAsString() {
		fail();
	}

	public void testSetConfigForRemotePull() {
		remoteOperations.init("a");
		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		assertEquals(Urls.compose(remoteRoot.getAbsolutePath(), "a"), localOperations.getConfig("a", "remote", "origin", "url"));
		assertEquals("+refs/heads/*:refs/remotes/origin/*", localOperations.getConfig("a", "remote", "origin", "fetch"));
		assertEquals("origin", localOperations.getConfig("a", "branch", "master", "remote"));
		assertEquals("refs/heads/master", localOperations.getConfig("a", "branch", "master", "merge"));

	}

	public void testPull() {
		IFileDescription plainAb = IFileDescription.Utils.plain("a/b");
		IFileDescription encryptedAb = IFileDescription.Utils.encrypted("a/c", "name", Crypto.makeKey());

		remoteOperations.init("a");
		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());

		checkPull(plainAb, v11);
		checkPull(encryptedAb, v11);
	}

	public void testGetFile() {
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());

		assertEquals(v11, remoteOperations.getFile(IFileDescription.Utils.plain("a/b")));
		assertEquals(v12, remoteOperations.getFile(IFileDescription.Utils.plain("a/b/c")));
		assertEquals(v21, remoteOperations.getFile(IFileDescription.Utils.plain("a/b/d")));

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		assertEquals(v11, localOperations.getFile(IFileDescription.Utils.plain("a/b")));
		assertEquals(v12, localOperations.getFile(IFileDescription.Utils.plain("a/b/c")));
		assertEquals(v21, localOperations.getFile(IFileDescription.Utils.plain("a/b/d")));
	}

	public void testGetFileAndDescendants() {
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/b"), v11);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v12);
		remoteOperations.put(IFileDescription.Utils.plain("a/b/d"), v21);
		remoteOperations.addAllAndCommit("a", getClass().getSimpleName());
		Map<String, Object> map = Maps.with(v11, "c", v12, "d", v21);

		assertEquals(map, remoteOperations.getFileAndDescendants(IFileDescription.Utils.plain("a/b")));
		assertEquals(v12, remoteOperations.getFileAndDescendants(IFileDescription.Utils.plain("a/b/c")));
		assertEquals(v21, remoteOperations.getFileAndDescendants(IFileDescription.Utils.plain("a/b/d")));

		localOperations.init("a");
		localOperations.setConfigForRemotePull("a", remoteRoot.getAbsolutePath());
		localOperations.pull("a");

		assertEquals(map, localOperations.getFileAndDescendants(IFileDescription.Utils.plain("a/b")));
		assertEquals(v12, localOperations.getFileAndDescendants(IFileDescription.Utils.plain("a/b/c")));
		assertEquals(v21, localOperations.getFileAndDescendants(IFileDescription.Utils.plain("a/b/d")));
	}

	private void checkPull(IFileDescription fileDescription, Map<String, Object> data) {
		IFileDescription.Utils.save(remoteRoot, fileDescription, data);
		File repositoryFile = fileDescription.findRepositoryUrl(remoteRoot);
		String repositoryUrl = Files.offset(remoteRoot, repositoryFile);

		File remoteRepositoryDirectory = new File(remoteRoot, repositoryUrl);
		File localRepositoryDirectory = new File(localRoot, repositoryUrl);

		remoteOperations.addAllAndCommit(repositoryUrl, GitOperationsTest.class.getSimpleName());

		assertTrue(remoteRepositoryDirectory.exists());
		assertTrue(localRepositoryDirectory.exists());
		assertEquals(data, IFileDescription.Utils.load(remoteRoot, fileDescription));
		assertFalse(fileDescription.getFile(localRoot).exists());

		localOperations.pull(repositoryUrl);

		assertEquals(data, IFileDescription.Utils.load(remoteRoot, fileDescription));
		assertEquals(data, IFileDescription.Utils.load(localRoot, fileDescription));
	}

}