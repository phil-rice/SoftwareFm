package org.softwarefm.server.configurator;

import org.easymock.EasyMock;
import org.softwarefm.server.configurator.UsageConfigurator;
import org.softwarefm.server.usage.IUsageCallback;
import org.softwarefm.server.usage.IUsageGetter;

public class UsageConfiguratorWithMocksTest extends AbstractUsageConfiguratorTest{

	private IUsageGetter getter;
	private IUsageCallback callback;

	public void testSet() throws Exception{
		callback.process("", "someUser", stats);
		EasyMock.replay(callback,getter);
		checkSet();
		EasyMock.verify(callback,getter);
	}
	
	public void testget() throws Exception{
		EasyMock.expect(getter.getStats("someUser")).andReturn(stats);
		EasyMock.replay(callback,getter);
		checkGet();
		EasyMock.verify(callback,getter);
		
	}
	
	@Override
	protected UsageConfigurator makeConfigurator() {
		return new UsageConfigurator(getter= EasyMock.createMock(IUsageGetter.class), callback= EasyMock.createMock(IUsageCallback.class), persistance);
	}

}
