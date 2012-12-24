package org.softwarefm.eclipse.usage;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.usage.internal.Usage;
import org.softwarefm.eclipse.usage.internal.UsagePersistance;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.maps.Maps;

public class UsagePersistanceTest extends TestCase {

	private final IUsagePersistance usagePersistance = new UsagePersistance();

	public void testLoadsEmptyUsageIfStringBlank() {
		IUsage usage = new Usage(IMultipleListenerList.Utils.defaultList());
		usage.selected("nuked by load");

		usagePersistance.populate(usage, null);
		assertEquals(0, usage.getStats().size());
		usage.selected("nuked by load2");

		usagePersistance.populate(usage, "");
		assertEquals(0, usage.getStats().size());
	}

	public void testSavesAndLoadsPersistanceData() {
		Usage usageOriginal = new Usage(IMultipleListenerList.Utils.defaultList());
		usageOriginal.selected("a");
		usageOriginal.selected("b");
		usageOriginal.selected("b");
		String saved = usagePersistance.save(usageOriginal);

		IUsage newUsage = new Usage(IMultipleListenerList.Utils.defaultList());
		newUsage.selected("nuked by load");
		usagePersistance.populate(newUsage, saved);
		assertEquals(Maps.makeMap("a", new UsageStats(1), "b", new UsageStats(2)), newUsage.getStats());
	}

	public void testListenersCalledByPopulate() {
		IUsageListener listener1 = EasyMock.createMock(IUsageListener.class);
		listener1.usageChanged();
		EasyMock.replay(listener1);

		Usage usage = new Usage(IMultipleListenerList.Utils.defaultList());
		usage.selected("a");
		usage.selected("b");
		usage.selected("b");
		String saved = usagePersistance.save(usage);
		
		Usage newUsage = new Usage(IMultipleListenerList.Utils.defaultList());
		newUsage.addUsageListener(listener1);
		usagePersistance.populate(newUsage, saved);

		EasyMock.verify(listener1);
	}

}
