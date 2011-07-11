package org.arc4eclipse.utilities.pooling;

import java.util.Arrays;
import java.util.List;

import org.arc4eclipse.utilities.maps.ArraySimpleMap;
import org.arc4eclipse.utilities.pooling.IPool;
import org.arc4eclipse.utilities.pooling.PoolOptions;

public class ArraySimpleMapPoolTest extends AbstractPoolTest<ArraySimpleMap<String, String>> {

	private final String key1 = "k1";
	private final String key2 = "k2";
	private final String key3 = "k3";
	private final List<String> keys = Arrays.asList(key1, key2, key3);
	private final List<String> values = Arrays.asList("v1", "v2", "v3");

	
	protected IPool<ArraySimpleMap<String, String>> makePool(PoolOptions poolOptions) {
		return IPool.Utils.makeArraySimpleMapPool(poolOptions, keys, String.class);
	}

	
	protected IPoolCleanTestCallback<ArraySimpleMap<String, String>> makeCleanTestCallback() {
		return new IPoolCleanTestCallback<ArraySimpleMap<String, String>>() {
			public void setData(ArraySimpleMap<String, String> item) {
				item.setValuesFrom(values);
			}

			public void checkDataHasBeenSet(ArraySimpleMap<String, String> item) {
				assertEquals("v1", item.get(key1));
				assertEquals("v2", item.get(key2));
				assertEquals("v3", item.get(key3));
			}

			public void checkDataBlank(ArraySimpleMap<String, String> item) {
				assertEquals(null, item.get(key1));
				assertEquals(null, item.get(key2));
				assertEquals(null, item.get(key3));
			}
		};
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	
	protected Class<ArraySimpleMap<String, String>> classBeingTested() {
		return (Class) ArraySimpleMap.class;
	}

}
