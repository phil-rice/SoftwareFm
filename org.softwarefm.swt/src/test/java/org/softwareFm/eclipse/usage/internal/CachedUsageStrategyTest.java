/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.httpClient.internal.MemoryResponseCallback;
import org.softwareFm.crowdsource.utilities.future.Futures;
import org.softwareFm.crowdsource.utilities.maps.IHasCache;
import org.softwareFm.jarAndClassPath.api.IUsageStrategy;
import org.softwareFm.jarAndClassPath.api.IUserDataManager;

public class CachedUsageStrategyTest extends TestCase {

	private IUserDataManager userDataManager;
	private IUsageStrategy delegate;
	private IUsageStrategy cached;
	private MemoryResponseCallback callback;
	private int period;
	private IHasCache cachesToClear;

	public void testOnlyCallsDelegateOnceForEachDigestBeforePeriodExpires() {
		addOneSetOfCalls();
		replayMocks();

		call();

		verifyMocks();
	}

	public void testCallsAgainAfterClearCaches() {
		addOneSetOfCalls();
		addOneSetOfCalls();
		replayMocks();

		call();
		((IHasCache) cached).clearCaches();
		call();

		verifyMocks();
	}

	public void testCallsAgainAfterPeriodExpires() throws InterruptedException {
		addOneSetOfCalls();
		addOneSetOfCalls();
		replayMocks();

		call();
		Thread.sleep(period + 50);
		call();

		verifyMocks();

	}

	public void testCallsAgainIfUserDataChanges() {
		addOneSetOfCalls();
		addOneSetOfCalls();
		replayMocks();

		call();
		userDataManager.setUserData(this, userDataManager.getUserData());
		call();

		verifyMocks();

	}

	protected void verifyMocks() {
		EasyMock.verify(delegate, cachesToClear);
	}

	protected void replayMocks() {
		EasyMock.replay(delegate, cachesToClear);
	}

	protected void call() {
		cached.using("sfmId", "digest1", callback);
		cached.using("sfmId", "digest2", callback);
		cached.using("sfmId", "digest2", callback);
		cached.using("sfmId", "digest1", callback);
	}

	protected void addOneSetOfCalls() {
		delegate.using("sfmId", "digest1", callback);
		EasyMock.expectLastCall().andReturn(Futures.doneFuture(null));
		cachesToClear.clearCaches();
		delegate.using("sfmId", "digest2", callback);
		EasyMock.expectLastCall().andReturn(Futures.doneFuture(null));
		cachesToClear.clearCaches();
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		callback = IResponseCallback.Utils.memoryCallback();
		delegate = EasyMock.createMock(IUsageStrategy.class);
		userDataManager = IUserDataManager.Utils.userDataManager();
		period = 300;
		cachesToClear = EasyMock.createMock(IHasCache.class);
		cached = IUsageStrategy.Utils.cached(delegate, period, cachesToClear, userDataManager);
	}

}