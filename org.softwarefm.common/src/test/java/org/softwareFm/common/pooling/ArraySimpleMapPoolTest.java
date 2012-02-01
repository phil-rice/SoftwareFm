/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.pooling;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.common.maps.ArraySimpleMap;
import org.softwareFm.common.pooling.IPool;
import org.softwareFm.common.pooling.PoolOptions;

public class ArraySimpleMapPoolTest extends AbstractPoolTest<ArraySimpleMap<String, String>> {

	private final String key1 = "k1";
	private final String key2 = "k2";
	private final String key3 = "k3";
	private final List<String> keys = Arrays.asList(key1, key2, key3);
	private final List<String> values = Arrays.asList("v1", "v2", "v3");

	@Override
	protected IPool<ArraySimpleMap<String, String>> makePool(PoolOptions poolOptions) {
		return IPool.Utils.makeArraySimpleMapPool(poolOptions, keys, String.class);
	}

	@Override
	protected IPoolCleanTestCallback<ArraySimpleMap<String, String>> makeCleanTestCallback() {
		return new IPoolCleanTestCallback<ArraySimpleMap<String, String>>() {
			@Override
			public void setData(ArraySimpleMap<String, String> item) {
				item.setValuesFrom(values);
			}

			@Override
			public void checkDataHasBeenSet(ArraySimpleMap<String, String> item) {
				assertEquals("v1", item.get(key1));
				assertEquals("v2", item.get(key2));
				assertEquals("v3", item.get(key3));
			}

			@Override
			public void checkDataBlank(ArraySimpleMap<String, String> item) {
				assertEquals(null, item.get(key1));
				assertEquals(null, item.get(key2));
				assertEquals(null, item.get(key3));
			}
		};
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Class<ArraySimpleMap<String, String>> classBeingTested() {
		return (Class) ArraySimpleMap.class;
	}

}