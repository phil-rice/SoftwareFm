package org.arc4eclipse.utilities.pooling;

import org.arc4eclipse.utilities.pooling.IPool;
import org.arc4eclipse.utilities.pooling.PoolOptions;

import junit.framework.TestCase;

public class ObjectPoolTest extends TestCase {

	public void testObjectPoolCreatesArraysOfTheSpecifiedClass() {
		IPool<String[]> pool = IPool.Utils.makeArrayPool(new PoolOptions(), String.class, 10);
		String[] array0 = pool.newObject();
		assertEquals(10, array0.length);
		array0[0] = "123";
	}

	public void testObjectPoolCleansWhenRequested() {
		checkObjectPoolCleansWhenRequested(false);
		checkObjectPoolCleansWhenRequested(true);
	}

	private void checkObjectPoolCleansWhenRequested(boolean clean) {
		IPool<String[]> pool = IPool.Utils.makeArrayPool(new PoolOptions().withCleanWhenReuse(clean), String.class, 10);
		String[] array0 = pool.newObject();
		array0[0] = "123";
		pool.dispose();
		String[] array1 = pool.newObject();
		if (clean)
			assertNull(array1[0]);
		else
			assertEquals("123", array1[0]);
	}

}
