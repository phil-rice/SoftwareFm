package org.arc4eclipse.utilities.pooling;

import org.arc4eclipse.utilities.pooling.IObjectDefinition;
import org.arc4eclipse.utilities.pooling.IPool;

import junit.framework.TestCase;

public abstract class PoolTest extends TestCase {

	protected IObjectDefinition<IExampleForPool> defn = new IObjectDefinition<IExampleForPool>() {
		public Class<IExampleForPool> objectClass() {
			return IExampleForPool.class;
		}

		public IExampleForPool createBlank() {
			return new ExampleForPool();
		}

		public void clean(IExampleForPool oldObject) {
			oldObject.setValue(0);
		}
	};

	public void testSizeIsZeroWhenPoolCreated() {
		IPool<IExampleForPool> mainPool = makePool();
		assertEquals(0, mainPool.size());
	}

	public void testSizeDoesntChangeWhenRead() {
		IPool<IExampleForPool> mainPool = makePool();
		assertEquals(0, mainPool.size());
		assertEquals(0, mainPool.size());
		assertEquals(0, mainPool.size());
		assertEquals(0, mainPool.size());
	}

	public void testMakesSeparateInstances() {
		IPool<IExampleForPool> pool = makePool();
		makeWithValues(pool, 1, 2, 3, 4, 5);
		checkValues(pool, 1, 2, 3, 4, 5);
	}

	public void testReusesObjectsAfterDispose() {
		IPool<IExampleForPool> pool = makePool();
		IExampleForPool[] firstValues = makeWithValues(pool, 1, 2, 3, 4, 5);
		checkValues(pool, 1, 2, 3, 4, 5);
		pool.dispose();
		IExampleForPool[] secondValues = makeWithoutValues(pool, 5);
		assert firstValues.length == secondValues.length;
		for (int i = 0; i < firstValues.length; i++)
			assertSame(firstValues[i], secondValues[i]);
	}

	public void testCleansObjectWhenAskedTo() {
		IPool<IExampleForPool> pool = makePool(true);
		makeWithValues(pool, 1, 2, 3, 4, 5);
		pool.dispose();
		makeWithoutValues(pool, 5);
		checkValues(pool, 0, 0, 0, 0, 0);
	}

	public void testDoesntCleansObjectWhenAskedNotTo() {
		IPool<IExampleForPool> pool = makePool(false);
		makeWithValues(pool, 1, 2, 3, 4, 5);
		pool.dispose();
		makeWithoutValues(pool, 5);
		checkValues(pool, 1, 2, 3, 4, 5);
	}

	private void checkValues(IPool<IExampleForPool> pool, int... values) {
		for (int i = 0; i < values.length; i++) {
			IExampleForPool instance = pool.getObject(i);
			assertEquals(values[i], instance.getValue());
		}
	}

	private IExampleForPool[] makeWithValues(IPool<IExampleForPool> pool, int... values) {
		IExampleForPool[] result = new IExampleForPool[values.length];
		for (int i = 0; i < result.length; i++) {
			IExampleForPool instance = pool.newObject();
			instance.setValue(values[i]);
			result[i] = instance;
		}
		return result;
	}

	private IExampleForPool[] makeWithoutValues(IPool<IExampleForPool> pool, int size) {
		IExampleForPool[] result = new IExampleForPool[size];
		for (int i = 0; i < result.length; i++) {
			IExampleForPool instance = pool.newObject();
			result[i] = instance;
		}
		return result;
	}

	abstract protected IPool<IExampleForPool> makePool(boolean cleanWhenReuse);

	protected IPool<IExampleForPool> makePool() {
		return makePool(true);
	}
}
