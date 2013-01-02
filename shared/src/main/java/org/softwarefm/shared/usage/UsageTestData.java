package org.softwarefm.shared.usage;

import java.util.Arrays;
import java.util.List;

import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.internal.Usage;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.maps.SimpleMaps;

public class UsageTestData {

	public static Usage usagea1b3;
	public static Usage usagea2b6;
	public static Usage usagea2b1;
	public static Usage usageb1c2;
	public static Usage usagec1d1;

	static {
		UsageTestData.usagea2b1 = new Usage();
		UsageTestData.usagea2b1.selected("a");
		UsageTestData.usagea2b1.selected("a");
		UsageTestData.usagea2b1.selected("b");
		UsageTestData.usageb1c2 = new Usage();
		UsageTestData.usageb1c2.selected("b");
		UsageTestData.usageb1c2.selected("c");
		UsageTestData.usageb1c2.selected("c");
		UsageTestData.usagea1b3 = new Usage();
		UsageTestData.usagea1b3.selected("a");
		UsageTestData.usagea1b3.selected("b");
		UsageTestData.usagea1b3.selected("b");
		UsageTestData.usagea1b3.selected("b");
		UsageTestData.usagea2b6 = new Usage();
		UsageTestData.usagea2b6.selected("a");
		UsageTestData.usagea2b6.selected("a");
		UsageTestData.usagea2b6.selected("b");
		UsageTestData.usagea2b6.selected("b");
		UsageTestData.usagea2b6.selected("b");
		UsageTestData.usagea2b6.selected("b");
		UsageTestData.usagea2b6.selected("b");
		UsageTestData.usagea2b6.selected("b");
		UsageTestData.usagec1d1 = new Usage();
		UsageTestData.usagec1d1.selected("c");
		UsageTestData.usagec1d1.selected("d");
	}
	public static IUsageStats statsa1b3 = usagea1b3.getStats();
	public static IUsageStats statsa2b6 = usagea2b6.getStats();
	public static IUsageStats statsa2b1 = usagea2b1.getStats();
	public static IUsageStats statsb1c2 = usageb1c2.getStats();
	public static IUsageStats statsc1d1 = usagec1d1.getStats();
	public static FriendData friend1 = new FriendData("fr1", null);
	public static FriendData friend2 = new FriendData("fr2", null);
	public static List<FriendData> friends = Arrays.asList(friend1, friend2);

	public static ISimpleMap<String, IUsageStats> fr1_a1b3_fr2_b1c2 = SimpleMaps.makeMap("fr1", statsa1b3, "fr2", statsb1c2);
	public static ISimpleMap<String, UsageStatData> a_fr1_1 = SimpleMaps.makeMap("fr1", new UsageStatData(1));

}
