package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.softwareFm.crowdsource.api.server.AbstractProcessCallTest;
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
		EasyMock.expect(repoNavigation.navigationData("/someUrl", ICallback.Utils.<Map<String,List<String>>>noCallback())).andReturn(ITransaction.Utils.<Map<String, List<String>>> doneTransaction(expected));
		EasyMock.replay(repoNavigation);
		checkStringResult(processor.process(new RequestLineMock(CommonConstants.POST, Urls.composeWithSlash(CommonConstants.navigationPrefix, "someUrl")), Maps.emptyStringObjectMap()), Json.toString(expected));
		EasyMock.verify(repoNavigation);
	}

	@Override
	protected NavigationCommandProcessor makeProcessor() {
		repoNavigation = EasyMock.createMock(IRepoNavigation.class);
		return new NavigationCommandProcessor(getServerUserAndGroupsContainer(), getUserCryptoAccess(), repoNavigation);
	}

}
