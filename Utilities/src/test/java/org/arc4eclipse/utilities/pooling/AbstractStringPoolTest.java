package org.arc4eclipse.utilities.pooling;

import org.arc4eclipse.utilities.pooling.IPool;
import org.arc4eclipse.utilities.strings.ISimpleStringWithSetters;

public abstract class AbstractStringPoolTest<T extends ISimpleStringWithSetters> extends AbstractPoolTest<T> {
	
	public void testMakesObjects() {
		IPool<T> pool = makePool();
		T object = pool.newObject();
		assertEquals(0, object.length());
	}

	
	protected IPoolCleanTestCallback<T> makeCleanTestCallback() {
		return new IPoolCleanTestCallback<T>() {
			public void setData(T item) {
				item.setFromString("abc");
			}

			public void checkDataBlank(T item) {
				assertEquals(0, item.length());
			}

			public void checkDataHasBeenSet(T item) {
				assertEquals("abc", item.asString());
			}
		};
	}

}
