/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.server.doers.internal.PostProcessor;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class PostProcessorTest extends AbstractProcessCallTest<PostProcessor> {

	public void testIgnoresNonePosts() {
		checkIgnoresNonePosts();
	}

	public void testSendsTheMapToTheGitServer() {
		remoteOperations.init("a");
		processor.process(makeRequestLine(CommonConstants.POST, "a/b"), makeDataMap(v11));
		checkContents(remoteRoot, "a/b", v11);
	}

	public void testCommitMessageIsUsed(){
		fail();
	}
	
	@SuppressWarnings("unchecked")
	public void testMergesTheNewDataWithOldData() {
		remoteOperations.init("a");
		processor.process(makeRequestLine(CommonConstants.POST, "a/b"), makeDataMap(v11));
		processor.process(makeRequestLine(CommonConstants.POST, "a/b"), makeDataMap(a1b2));
		checkContents(remoteRoot, "a/b", Maps.<String, Object> merge(v11, a1b2));
	}

	public void testNewDataReplacesOld() {
		remoteOperations.init("a");
		processor.process(makeRequestLine(CommonConstants.POST, "a/b"), makeDataMap(v11));
		processor.process(makeRequestLine(CommonConstants.POST, "a/b"), makeDataMap(Maps.stringObjectMap("v", 2)));
		checkContents(remoteRoot, "a/b", v12);
	}

	public void testDontCopySubDirectoriesIntoFile() {
		remoteOperations.init("a");
		processor.process(makeRequestLine(CommonConstants.POST, "a/b/c"), makeDataMap(a1b2));
		processor.process(makeRequestLine(CommonConstants.POST, "a/b/c/d"), makeDataMap(v22));

		processor.process(makeRequestLine(CommonConstants.POST, "a/b"), makeDataMap(v11));
		processor.process(makeRequestLine(CommonConstants.POST, "a/b"), makeDataMap(Maps.stringObjectMap("v", 2)));

		checkContents(remoteRoot, "a/b", v12);

	}


	@Override
	protected PostProcessor makeProcessor() {
		return new PostProcessor(getServerApi().makeContainer().gitOperations());
	}

}