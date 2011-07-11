package org.arc4eclipse.utilities.pooling;

import junit.framework.TestCase;

import org.arc4eclipse.utilities.pooling.IPool;
import org.arc4eclipse.utilities.pooling.PoolOptions;

public abstract class AbstractPoolTest<T> extends TestCase {

	abstract protected IPool<T> makePool(PoolOptions poolOptions);

	abstract protected IPoolCleanTestCallback<T> makeCleanTestCallback();

	abstract protected Class<? extends T> classBeingTested();

	public void testMakesObjects() {
		IPool<T> pool = makePool();
		T item1 = pool.newObject();
		assertTrue(classBeingTested().isAssignableFrom(item1.getClass()));
	}

	public void testReusesObjects() {
		IPool<T> pool = makePool();
		T item1 = pool.newObject();
		pool.dispose();
		T item2 = pool.newObject();
		assertSame(item1, item2);
	}

	public void testObeysClean() {
		checkObeysClean(false);
		checkObeysClean(true);
	}

	private void checkObeysClean(boolean clean) {
		IPoolCleanTestCallback<T> callback = makeCleanTestCallback();
		IPool<T> pool = makePool(new PoolOptions().withCleanWhenReuse(clean));
		T item0 = pool.newObject();
		callback.setData(item0);
		callback.checkDataHasBeenSet(item0);
		pool.dispose();
		T item1 = pool.newObject();
		if (clean)
			callback.checkDataBlank(item1);
		else
			callback.checkDataHasBeenSet(item1);
	}

	protected IPool<T> makePool() {
		return makePool(new PoolOptions());
	}

}
