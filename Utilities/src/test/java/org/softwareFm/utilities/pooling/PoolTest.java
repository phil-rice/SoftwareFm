/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.pooling;

import junit.framework.TestCase;

public abstract class PoolTest extends TestCase {

	protected IObjectDefinition<IExampleForPool> defn = new IObjectDefinition<IExampleForPool>() {
		@Override
		public Class<IExampleForPool> objectClass() {
			return IExampleForPool.class;
		}

		@Override
		public IExampleForPool createBlank() {
			return new ExampleForPool();
		}

		@Override
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