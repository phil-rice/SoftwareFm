package org.softwareFm.eclipse.user;

public interface IProjectTimeGetter {

	String thisMonth();

	int day();
	
	Iterable<String> lastNMonths(int n);

}
