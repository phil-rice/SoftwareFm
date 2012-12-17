package org.softwarefm.eclipse.usage.internal;

import junit.framework.TestCase;

import org.softwarefm.eclipse.usage.IUsage;
import org.softwarefm.eclipse.usage.IUsagePersistance;
import org.softwarefm.eclipse.usage.UsageStats;
import org.softwarefm.utilities.maps.Maps;

public class UsagePersistanceTest extends TestCase {

	private final IUsagePersistance usagePersistance = new UsagePersistance();

	public void testLoadsEmptyUsageIfStringBlank() {
		IUsage usageNull = usagePersistance.load(null);
		assertEquals(0, usageNull.getStats().size());
		
		IUsage usageEmpty = usagePersistance.load("");
		assertEquals(0, usageEmpty.getStats().size());
	}


	public void testSavesAndLoadsPersistanceData() {
		Usage usage = new Usage();
		usage.selected("a");
		usage.selected("b");
		usage.selected("b");
		String saved = usagePersistance.save(usage);
		IUsage newUsage = usagePersistance.load(saved);
		assertEquals(Maps.makeMap("a", new UsageStats(1), "b", new UsageStats(2)), newUsage.getStats());
	}

	public void testListener(){
		fail();
	}

}
