package org.softwarefm.eclipse.usage;

import org.softwarefm.eclipse.usage.internal.UsagePersistance;


public interface IUsagePersistance {

	IUsage load(String text);

	String save(IUsage usage);

	public static class Utils{
		public static IUsagePersistance persistance(){
			return new UsagePersistance();
		}
	}
}
