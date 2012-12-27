package org.softwarefm.shared.usage;

import org.softwarefm.eclipse.usage.IUsageStats;
import org.softwarefm.eclipse.usage.internal.Usage;
import org.softwarefm.utilities.events.IMultipleListenerList;

public class UsageTestData {

	public static Usage usagea1b3;
	public static Usage usagea2b1;
	public static Usage usageb1c2;
	public static Usage usagec1d1;

	static {
		UsageTestData.usagea2b1 = new Usage(IMultipleListenerList.Utils.defaultList());
		UsageTestData.usagea2b1.selected("a");
		UsageTestData.usagea2b1.selected("a");
		UsageTestData.usagea2b1.selected("b");
		UsageTestData.usageb1c2 = new Usage(IMultipleListenerList.Utils.defaultList());
		UsageTestData.usageb1c2.selected("b");
		UsageTestData.usageb1c2.selected("b");
		UsageTestData.usageb1c2.selected("c");
		UsageTestData.usagea1b3 = new Usage(IMultipleListenerList.Utils.defaultList());
		UsageTestData.usagea1b3.selected("a");
		UsageTestData.usagea1b3.selected("b");
		UsageTestData.usagea1b3.selected("b");
		UsageTestData.usagea1b3.selected("b");
		UsageTestData.usagec1d1 = new Usage(IMultipleListenerList.Utils.defaultList());
		UsageTestData.usagec1d1.selected("c");
		UsageTestData.usagec1d1.selected("d");
	}
	public static IUsageStats statsa1b3 = usagea1b3.getStats();
	public static IUsageStats statsa2b1 = usagea2b1.getStats();
	public static IUsageStats statsb1c2 = usageb1c2.getStats();
	public static IUsageStats statsc1d1 = usagec1d1.getStats();

}
