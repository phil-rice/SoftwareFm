/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.server.RequestLineMock;
import org.softwareFm.crowdsource.navigation.IRepoNavigation;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class NavigationCommandProcessorTest extends AbstractProcessCallTest<NavigationCommandProcessor> {

	private IRepoNavigation repoNavigation;

	public void testOnlyAcceptsPost() {
		checkIgnoresNonePosts();
		checkIgnores(CommonConstants.POST, "someUri");
	}

	public void test() {
		Map<String, List<String>> expected = Maps.<String, List<String>> makeMap("some", "Result");
		EasyMock.expect(repoNavigation.navigationData("/someUrl", ICallback.Utils.<Map<String, List<String>>> noCallback())).andReturn(ITransaction.Utils.<Map<String, List<String>>> doneTransaction(expected));
		EasyMock.replay(repoNavigation);
		IProcessResult.Utils.	checkStringResult(processor.process(new RequestLineMock(CommonConstants.POST, Urls.composeWithSlash(CommonConstants.navigationPrefix, "someUrl")), Maps.emptyStringObjectMap()), Json.toString(expected));
		EasyMock.verify(repoNavigation);
	}

	@Override
	protected NavigationCommandProcessor makeProcessor() {
		repoNavigation = EasyMock.createMock(IRepoNavigation.class);
		return new NavigationCommandProcessor(getServerUserAndGroupsContainer(), getUserCryptoAccess(), repoNavigation);
	}

}