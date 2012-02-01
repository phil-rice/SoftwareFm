/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.pooling;

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