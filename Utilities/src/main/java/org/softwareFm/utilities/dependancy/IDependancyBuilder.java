package org.softwareFm.utilities.dependancy;

import org.softwareFm.utilities.dependancy.impl.DependancyBuilder;
import org.softwareFm.utilities.exceptions.LoopException;

public interface IDependancyBuilder<T> extends IDependancy<T> {

	IDependancyBuilder<T> parent(T child, T parent) throws LoopException;

	public static class Utils {
		public static <T> IDependancyBuilder<T> newBuilder() {
			return new DependancyBuilder<T>();
		}
	}
}
