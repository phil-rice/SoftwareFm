/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.io.File;
import java.util.Collections;

import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.maps.UrlCache;

public class MakeRootProcessorTest extends AbstractProcessCallTest<MakeRootProcessor> {

	public void testIgnoresNoneGets() {
		checkIgnoresNonePosts();
	}

	public void testMakesRepository() {
		checkProcessMakesRepo("a/b/c");
		checkProcessMakesRepo("a/b/d");
		checkProcessMakesRepo("b/d");
		checkProcessMakesRepo("c");
	}

	public void testOKWhenRepositoryAlreadyExists() {
		checkProcessMakesRepo("a/b/c");
		checkProcessRemakeRepo("a/b/c");
		checkProcessMakesRepo("b/d");
		checkProcessRemakeRepo("b/d");
	}

	public void testThrowsExceptionWhenTryingToCreateUnderNewRepository() {
		checkProcessMakesRepo("a/b");
		IProcessResult result = processor.process(makeRequestLine(CommonConstants.POST, "/" + CommonConstants.makeRootPrefix + "/" + "a/b/c"), makeDataMap(Collections.<String, Object> emptyMap()));
		IProcessResult.Utils.	checkErrorResult(result, CommonConstants.notFoundStatusCode, "Cannot create git /a/b/c under second repository", "Cannot create git /a/b/c under second repository");

		checkRepositoryDoesntExists(new File(remoteRoot, "a/b/c"));
	}

	private void checkProcessRemakeRepo(String uri) {
		checkRepositoryExists(remoteRoot, uri);
		processor.process(makeRequestLine(CommonConstants.POST, "/" + CommonConstants.makeRootPrefix + "/" + uri), makeDataMap(Collections.<String, Object> emptyMap()));
		checkRepositoryExists(remoteRoot, uri);
	}

	private void checkProcessMakesRepo(String uri) {
		File file = new File(remoteRoot, uri);
		checkRepositoryDoesntExists(file);
		processor.process(makeRequestLine(CommonConstants.POST, "/" + CommonConstants.makeRootPrefix + "/" + uri), makeDataMap(Collections.<String, Object> emptyMap()));
		checkRepositoryExists(remoteRoot, uri);
	}

	@Override
	protected MakeRootProcessor makeProcessor() {
		return new MakeRootProcessor(new UrlCache<String>(), getServerApi().makeContainer());
	}

}