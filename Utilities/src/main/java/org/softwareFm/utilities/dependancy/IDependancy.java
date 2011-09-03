package org.softwareFm.utilities.dependancy;

import java.util.List;

public interface IDependancy<T> {

	boolean dependsOn(T child, T parent);

	List<T> path(T child, T parent);

	ITopologicalSortResult<T> sort();

}
