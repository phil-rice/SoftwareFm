package org.softwareFm.eclipse.user;

import java.util.Arrays;

public class ProjectTimeGetterFixture implements IProjectTimeGetter {

	@Override
	public String thisMonth() {
		throw new IllegalStateException();
	}

	@Override
	public int day() {
		throw new IllegalStateException();
	}

	@Override
	public Iterable<String> lastNMonths(int n) {
		return Arrays.asList("month1", "month2", "month3");
	}

}
