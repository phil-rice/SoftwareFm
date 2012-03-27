/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.maps.UrlCache;

public class GitGetProcessorTest extends AbstractProcessCallTest<GitGetProcessor> {

	public void testIgnoresNoneGet() {
		checkIgnoresNoneGet();
	}

	public void testGetReturnsRepoAddressIfBelowOrAtRepository() {
		checkCreateRepository(remoteOperations, "a/b");
		checkGetFromProcessor("a/b/c", CommonConstants.repoUrlKey, "a/b");
		checkGetFromProcessor("a/b", CommonConstants.repoUrlKey, "a/b");
	}

	public void testGetReturnsDataIfAboveRespository() {
		checkCreateRepository(remoteOperations, "a/b/c");
		checkGetFromProcessor("a", CommonConstants.dataKey, Maps.stringObjectLinkedMap("b", emptyMap));
		checkGetFromProcessor("a/d", CommonConstants.dataKey, emptyMap);
	}

	@Override
	protected GitGetProcessor makeProcessor() {
		UrlCache<String> cache = new UrlCache<String>();
		return new GitGetProcessor(getServerContainer(), cache, CommonConstants.testTimeOutMs);
	}

}