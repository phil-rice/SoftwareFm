package org.softwarefm.eclipse.usage;

import org.softwarefm.eclipse.usage.internal.UsagePersistance;


public interface IUsagePersistance {

	void populate(IUsage usage, String text);

	String save(IUsage usage);

	public static class Utils{
		public static IUsagePersistance persistance(){
			return new UsagePersistance();
		}
	}
}
