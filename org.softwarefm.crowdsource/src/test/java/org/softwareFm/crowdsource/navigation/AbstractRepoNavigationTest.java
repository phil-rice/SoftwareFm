/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.navigation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.MemoryCallback;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;

abstract public class AbstractRepoNavigationTest extends ApiTest {

	protected static final Map<String, Object> a = Maps.stringObjectLinkedMap("a", Arrays.asList("b", "h"));
	protected static final Map<String, Object> b = Maps.stringObjectLinkedMap("a/b", Arrays.asList("c", "d", "e"));
	protected static final Map<String, Object> c = Maps.stringObjectLinkedMap("a/b/c", Arrays.asList());
	protected static final Map<String, Object> d = Maps.stringObjectLinkedMap("a/b/d", Arrays.asList());
	protected static final Map<String, Object> e = Maps.stringObjectLinkedMap("a/b/e", Arrays.asList("f", "g"));
	protected static final Map<String, Object> f = Maps.stringObjectLinkedMap("a/b/e/f", Arrays.asList());
	protected static final Map<String, Object> g = Maps.stringObjectLinkedMap("a/b/e/g", Arrays.asList());
	protected static final Map<String, Object> h = Maps.stringObjectLinkedMap("a/h", Arrays.asList("i", "j"));
	protected static final Map<String, Object> i = Maps.stringObjectLinkedMap("a/h/i", Arrays.asList());
	protected static final Map<String, Object> j = Maps.stringObjectLinkedMap("a/h/j", Arrays.asList());

	protected static final String[] urls = new String[] { "a", "a/b", "a/b/c", "a/b/d", "a/b/e/f", "a/b/e/g", "a/h", "a/h/i", "a/h/j", "k" };

	public static void setUpFixture(IGitOperations remoteOperations) {
		remoteOperations.init("a/b/c");
		remoteOperations.init("a/b/d");
		remoteOperations.init("a/b/e/f");
		remoteOperations.init("a/b/e/g");
		remoteOperations.init("a/h/i");
		remoteOperations.init("a/h/j");
	}

	@SuppressWarnings("unchecked")
	public static void checkValues(IRepoNavigation navigation) {
		checkGetUrl(navigation, "a", a);
		checkGetUrl(navigation, "a/b", a, b);
		checkGetUrl(navigation, "a/b/c", a, b, c);
		checkGetUrl(navigation, "a/b/d", a, b, d);
		checkGetUrl(navigation, "a/b/e/f", a, b, e, f);
		checkGetUrl(navigation, "a/b/e/g", a, b, e, g);
		checkGetUrl(navigation, "a/h", a, h);
		checkGetUrl(navigation, "a/h/i", a, h, i);
		checkGetUrl(navigation, "a/h/j", a, h, j);
		checkGetUrl(navigation, "k", Maps.stringObjectMap("k", Arrays.asList()));
	}

	public static void checkGetUrl(IRepoNavigation navigation, String url, Map<String, Object>... expectedFragments) {
		Map<String, Object> expected = Maps.merge(expectedFragments);
		MemoryCallback<Map<String, List<String>>> memory = ICallback.Utils.memory();
		ITransaction<Map<String, List<String>>> transaction = navigation.navigationData(url, memory);
		Map<String, List<String>> actualFromGet = transaction.get();
		assertEquals(expected, actualFromGet);
		assertEquals(expected, memory.getOnlyResult());
	}

}