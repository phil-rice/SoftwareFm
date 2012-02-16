/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.requests.MemoryResponseCallback;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.maps.IHasCache;
import org.softwareFm.eclipse.usage.IUsageStrategy;
import org.softwareFm.swt.explorer.IUserDataManager;

public class CachedUsageStrategyTest extends TestCase{

	private IUserDataManager userDataManager;
	private IUsageStrategy delegate;
	private IUsageStrategy cached;
	private MemoryResponseCallback callback;
	private int period;

	public void testOnlyCallsDelegateOnceForEachDigestBeforePeriodExpires() {
		addOneSetOfCalls();
		EasyMock.replay(delegate);
		
		call();
		
		EasyMock.verify(delegate);
	}

	public void testCallsAgainAfterClearCaches(){
		addOneSetOfCalls();
		addOneSetOfCalls();
		EasyMock.replay(delegate);
		
		call();
		((IHasCache)cached).clearCaches();
		call();
		
		EasyMock.verify(delegate);
	}
	
	public void testCallsAgainAfterPeriodExpires() throws InterruptedException{
		addOneSetOfCalls();
		addOneSetOfCalls();
		EasyMock.replay(delegate);
		
		call();
		Thread.sleep(period+50);
		call();
		
		EasyMock.verify(delegate);
		
	}
	
	public void testCallsAgainIfUserDataChanges(){
		addOneSetOfCalls();
		addOneSetOfCalls();
		EasyMock.replay(delegate);
		
		call();
		userDataManager.setUserData(this, userDataManager.getUserData());
		call();
		
		EasyMock.verify(delegate);
		
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
		delegate.using("sfmId", "digest2", callback);
		EasyMock.expectLastCall().andReturn(Futures.doneFuture(null));
	}
	
	
	public void testClearsCacheAfterPeriod(){
		
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		callback = IResponseCallback.Utils.memoryCallback();
		delegate = EasyMock.createMock(IUsageStrategy.class);
		userDataManager = IUserDataManager.Utils.userDataManager();
		period = 300;
		cached = IUsageStrategy.Utils.cached(delegate, period, userDataManager);
	}

}