package org.arc4eclipse.utilities.pooling;

import org.arc4eclipse.utilities.pooling.IPool;
import org.arc4eclipse.utilities.pooling.PoolOptions;

public class UnsafePoolTest extends PoolTest {
	
	protected IPool<IExampleForPool> makePool(boolean cleanWhenReuse) {
		return IPool.Utils.pool(new PoolOptions().withCleanWhenReuse(cleanWhenReuse), defn);
	}

}
