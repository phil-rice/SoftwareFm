/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.git.internal;

import java.io.File;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitWriter;
import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.git.internal.HttpGitWriter;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class HttpGitWriterTest extends AbstractProcessorDatabaseIntegrationTests {

	private IGitWriter gitWriter;
	private File remoteAbDotGit;
	private File remoteAbcData;
	private File remoteAbdData;



	public void testInit() {
		gitWriter.init("a/b");
		assertTrue(remoteAbDotGit.exists());
	}

	public void testPut() {
		gitWriter.init("a/b");
		gitWriter.put(IFileDescription.Utils.plain("a/b/c"), v11);
		assertEquals(v11, Json.parseFile(remoteAbcData));
	}

	public void testInitPutAllowsGet() {
		gitWriter.init("a/b");
		gitWriter.put(IFileDescription.Utils.plain("a/b/c"), v11);

		localOperations.init("a/b");
		localOperations.setConfigForRemotePull("a/b", remoteRoot.getAbsolutePath());
		localOperations.pull("a/b");
		assertEquals(v11, localOperations.getFile(IFileDescription.Utils.plain("a/b/c")));
	}

	public void testDelete() {
		gitWriter.init("a/b");
		gitWriter.put(IFileDescription.Utils.plain("a/b/c"), v11);
		gitWriter.put(IFileDescription.Utils.plain("a/b/d"), v12);

		gitWriter.delete(IFileDescription.Utils.plain("a/b/c"));

		assertFalse(remoteAbcData.exists());
		assertEquals(v12, Json.parseFile(remoteAbdData));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gitWriter = new HttpGitWriter(getHttpClient());
		remoteAbcData = new File(remoteRoot, Urls.compose("a/b/c", CommonConstants.dataFileName));
		remoteAbdData = new File(remoteRoot, Urls.compose("a/b/d", CommonConstants.dataFileName));
		remoteAbDotGit = new File(remoteRoot, Urls.compose("a/b", CommonConstants.DOT_GIT));
	}

}