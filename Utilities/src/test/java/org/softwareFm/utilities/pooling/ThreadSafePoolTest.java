package org.softwareFm.utilities.pooling;


public class ThreadSafePoolTest extends PoolTest {

	
	protected IPool<IExampleForPool> makePool(boolean cleanWhenReuse) {
		return IPool.Utils.pool(new PoolOptions().withCleanWhenReuse(cleanWhenReuse), defn);
	}

}
