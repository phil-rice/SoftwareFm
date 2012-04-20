/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.navigation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

public class RepoNavigationMockTest extends TestCase {

	private static final Map<String, Object> dataA = Maps.stringObjectMap("b", Maps.stringObjectMap("error if seen", 1), "d", Maps.stringObjectMap("error if seen", 1), "ignore", 1);
	private static final Map<String, Object> dataAb = Maps.stringObjectMap("c", Maps.stringObjectMap("error if seen", 1), "q", 1);
	private static final Map<String, Object> dataAd = Maps.stringObjectMap("e", Maps.stringObjectMap("error if seen", 1));
	private static final Map<String, Object> dataAbc = Maps.stringObjectMap("c", 1);

	public void test() {
		RepoNavigationMock navigation = new RepoNavigationMock("a", dataA, "a/b", dataAb, "a/b/c", dataAbc, "a/d", dataAd);
		check(navigation, "a/b", "a", Arrays.asList("b", "d"), "a/b", Arrays.asList("c"));
		check(navigation, "a/b/c", "a", Arrays.asList("b", "d"), "a/b", Arrays.asList("c"), "a/b/c", Arrays.asList());

	}

	@SuppressWarnings("rawtypes")
	private void check(RepoNavigationMock navigation, String url, Object... expected) {
		Map expectedMap = Maps.makeMap(expected);
		final AtomicReference<Map<String, List<String>>> callbackValue = new AtomicReference<Map<String, List<String>>>();
		ITransaction<Map<String, List<String>>> transaction = navigation.navigationData(url, new ICallback<Map<String, List<String>>>() {
			@Override
			public void process(Map<String, List<String>> map) throws Exception {
				callbackValue.set(map);
			}
		});
		assertTrue(transaction.isDone());
		assertEquals(expectedMap, transaction.get());
		assertEquals(expectedMap, callbackValue.get());
	}

}