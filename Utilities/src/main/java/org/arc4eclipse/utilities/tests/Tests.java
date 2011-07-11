package org.arc4eclipse.utilities.tests;

import junit.framework.Assert;

public class Tests {

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> E assertThrows(Class<E> class1, Runnable runnable) {
		try {
			runnable.run();
			Assert.fail(class1.getSimpleName() + " not thrown");
		} catch (Throwable e) {
			if (class1.isAssignableFrom(e.getClass()))
				return (E) e;
			Assert.fail(e.toString());
		}
		return null;
	}

}
