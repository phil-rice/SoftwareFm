package org.softwarefm.shared.usage;

import junit.framework.TestCase;

import org.softwarefm.shared.usage.internal.UsagePersistance;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.tests.Tests;

public class UsagePersistanceTest extends TestCase {

	private final IUsagePersistance usagePersistance = new UsagePersistance();

	public void testLoadsEmptyUsageIfStringBlank() {
		ISimpleMap<String, UsageStatData> nullMap = usagePersistance.parse( null);
		assertEquals(0, nullMap.keys().size());

		ISimpleMap<String, UsageStatData> emptyMap = usagePersistance.parse( "");
		assertEquals(0, emptyMap.keys().size());
		ISimpleMap<String, IUsageStats> nullFriendsMap = usagePersistance.parseFriendsUsage( null);
		assertEquals(0, nullFriendsMap.keys().size());
		
		ISimpleMap<String, IUsageStats> emptyFriendsMap = usagePersistance.parseFriendsUsage( "");
		assertEquals(0, emptyFriendsMap.keys().size());
	}

	public void testSavesAndLoadsPersistanceData() {
		checkSaveLoad(UsageTestData.statsa1b3);
		checkSaveLoad(UsageTestData.statsa2b1);
		checkSaveLoad(UsageTestData.statsc1d1);
	}

	public void testSavesAndLoadsFriendsUsage(){
		checkSaveLoad(UsageTestData.fr1_a1b3_fr2_b1c2);
	}
	
	private void checkSaveLoad(ISimpleMap<String, IUsageStats> friendsUsage) {
		String string = usagePersistance.saveFriendsUsage(friendsUsage);
		ISimpleMap<String, IUsageStats> newValue = usagePersistance.parseFriendsUsage(string);
		assertNotSame(newValue, friendsUsage);
		Tests.assertEquals(friendsUsage, newValue);
	}

	private void checkSaveLoad(IUsageStats original) {
		String string = usagePersistance.saveUsageStats(original);
		ISimpleMap<String, UsageStatData> newValue = usagePersistance.parse(string);
		assertNotSame(newValue, original);
		Tests.assertEquals(original, newValue);
	}


}
