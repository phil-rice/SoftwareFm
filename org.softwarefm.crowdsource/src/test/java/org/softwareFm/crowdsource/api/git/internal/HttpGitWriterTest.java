/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.git.internal;

import java.io.File;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.git.internal.HttpGitWriter;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class HttpGitWriterTest extends AbstractProcessorDatabaseIntegrationTests {

	private HttpGitWriter httpGitWriter;
	private File remoteAbDotGit;
	private File remoteAbcData;
	private File remoteAbdData;

	public void testInit() {
		httpGitWriter.init("a/b", "init");
		assertTrue(remoteAbDotGit.exists());
	}

	public void testPut() {
		httpGitWriter.init("a/b", "init");
		httpGitWriter.put(IFileDescription.Utils.plain("a/b/c"), v11, "v11->a/b/c");
		assertEquals(v11, Json.parseFile(remoteAbcData));
	}

	public void testInitPutAllowsGet() {
		httpGitWriter.init("a/b", "init");
		httpGitWriter.put(IFileDescription.Utils.plain("a/b/c"), v11, "v11->a/b/c");

		localOperations.init("a/b");
		localOperations.setConfigForRemotePull("a/b", remoteRoot.getAbsolutePath());
		localOperations.pull("a/b");
		assertEquals(v11, localOperations.getFile(IFileDescription.Utils.plain("a/b/c")));
	}

	public void testDelete() {
		httpGitWriter.init("a/b", "init");
		httpGitWriter.put(IFileDescription.Utils.plain("a/b/c"), v11, "v11->a/b/c");
		httpGitWriter.put(IFileDescription.Utils.plain("a/b/d"), v12, "v12->a/b/d");

		httpGitWriter.delete(IFileDescription.Utils.plain("a/b/c"), "delete a/b/c");

		assertFalse(remoteAbcData.exists());
		assertEquals(v12, Json.parseFile(remoteAbdData));
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		httpGitWriter = new HttpGitWriter(getHttpClient());
		remoteAbcData = new File(remoteRoot, Urls.compose("a/b/c", CommonConstants.dataFileName));
		remoteAbdData = new File(remoteRoot, Urls.compose("a/b/d", CommonConstants.dataFileName));
		remoteAbDotGit = new File(remoteRoot, Urls.compose("a/b", CommonConstants.DOT_GIT));
	}

}