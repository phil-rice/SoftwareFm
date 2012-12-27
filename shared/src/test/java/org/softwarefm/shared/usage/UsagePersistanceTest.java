package org.softwarefm.shared.usage;

import junit.framework.TestCase;

import org.softwarefm.eclipse.usage.IUsagePersistance;
import org.softwarefm.eclipse.usage.IUsageStats;
import org.softwarefm.eclipse.usage.UsageStatData;
import org.softwarefm.eclipse.usage.internal.UsagePersistance;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.tests.Tests;

public class UsagePersistanceTest extends TestCase {

	private final IUsagePersistance usagePersistance = new UsagePersistance();

	public void testLoadsEmptyUsageIfStringBlank() {

		ISimpleMap<String, UsageStatData> nullMap = usagePersistance.populate( null);
		assertEquals(0, nullMap.keys().size());

		ISimpleMap<String, UsageStatData> emptyMap = usagePersistance.populate( "");
		assertEquals(0, emptyMap.keys().size());
	}

	public void testSavesAndLoadsPersistanceData() {
		checkSaveLoad(UsageTestData.statsa1b3);
		checkSaveLoad(UsageTestData.statsa2b1);
		checkSaveLoad(UsageTestData.statsc1d1);
	}

	private void checkSaveLoad(IUsageStats original) {
		String string = usagePersistance.save(original);
		ISimpleMap<String, UsageStatData> newValue = usagePersistance.populate(string);
		assertNotSame(newValue, original);
		Tests.assertEquals(original, newValue);
	}


}
