/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.navigation;

import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;

public class HttpRepoNavigationTest extends AbstractProcessorDatabaseIntegrationTests {

	public void testHttpRepo() {
		HttpRepoNavigation navigation = new HttpRepoNavigation(getLocalUserAndGroupsContainer());
		AbstractRepoNavigationTest.checkValues(navigation);
		AbstractRepoNavigationTest.checkValues(navigation);
	}

	public void testFromLocalContainer() {
		final AtomicInteger count = new AtomicInteger();
		getLocalContainer().access(IRepoNavigation.class, new ICallback<IRepoNavigation>() {
			@Override
			public void process(IRepoNavigation navigation) throws Exception {
				count.incrementAndGet();
				AbstractRepoNavigationTest.checkValues(navigation);
				AbstractRepoNavigationTest.checkValues(navigation);
			}
		}).get();
		assertEquals(1, count.get());
		getLocalContainer().access(MeteredRepoNavigation.class, new ICallback<MeteredRepoNavigation>() {
			@Override
			public void process(MeteredRepoNavigation navigation) throws Exception {
				count.incrementAndGet();
				for (String url : AbstractRepoNavigationTest.urls)
					assertEquals(1, navigation.countFor(url));
				AbstractRepoNavigationTest.checkValues(navigation);
				for (String url : AbstractRepoNavigationTest.urls)
					assertEquals(2, navigation.countFor(url));
				AbstractRepoNavigationTest.checkValues(navigation);
				for (String url : AbstractRepoNavigationTest.urls)
					assertEquals(3, navigation.countFor(url));
			}
		}).get();
		assertEquals(2, count.get());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		AbstractRepoNavigationTest.setUpFixture(remoteOperations);
	}
}