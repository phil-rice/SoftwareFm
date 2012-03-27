/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.io.File;
import java.util.Collections;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.server.doers.internal.DeleteProcessor;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;

public class DeleteProcessorTest extends AbstractProcessCallTest<DeleteProcessor> {

	public void testIgnoresNoneDelete() {
		checkIgnores(CommonConstants.POST);
		checkIgnores(CommonConstants.GET);
	}

	public void testDeletesFileAndCommitsTheDeletion() {
		checkCreateRepository(remoteOperations, "a/b");
		remoteOperations.put(IFileDescription.Utils.plain("a/b/c"), v11);
		checkContents(remoteRoot, "a/b/c", v11);

		processor.process(makeRequestLine(CommonConstants.DELETE, "a/b/c"), Collections.<String, Object> emptyMap());

		checkContentsDontExist(remoteRoot, "a/b/c");
		
		createAndPull("a/b");
		checkContentsDontExist(localRoot, "a/b/c");
	}

	private void checkContentsDontExist(File root, String url) {
		File directory = new File(root, url);
		File file = new File(directory, CommonConstants.dataFileName);
		assertFalse(file.exists());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected DeleteProcessor makeProcessor() {
		return new DeleteProcessor(getServerContainer(), CommonConstants.testTimeOutMs);
	}

}