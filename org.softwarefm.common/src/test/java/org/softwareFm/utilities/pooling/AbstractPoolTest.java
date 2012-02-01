/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.pooling;

import junit.framework.TestCase;

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