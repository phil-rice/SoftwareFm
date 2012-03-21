/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.maps;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import junit.framework.Assert;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.UtilityConstants;
import org.softwareFm.crowdsource.utilities.constants.UtilityMessages;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.indent.Indent;

public class Maps {

	public static <K, V> Map<K, V> withOnly(Map<K, V> map, K... keys) {
		Map<K, V> result = Maps.newMapOfSameTypeMap(map);
		for (K key : keys)
			result.put(key, map.get(key));
		return result;
	}

	@SuppressWarnings("unchecked")
	public static MapAsList toMapAsList(String keyName, Map<String, Object> map, String sortOrder, List<String> keyOrder) {
		List<String> titles = Lists.newList();
		titles.add(keyName);
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() instanceof Map<?, ?>)
				for (String key : ((Map<String, Object>) entry.getValue()).keySet())
					if (!titles.contains(key))
						titles.add(key);
		}
		Collections.sort(titles, Lists.orderedComparator(keyOrder));
		int keyIndex = titles.indexOf(keyName);
		List<List<Object>> values = Lists.newList();
		for (Entry<String, Object> entry : map.entrySet()) {
			List<Object> theseValues = Lists.newList();
			String key = entry.getKey();
			Map<String, Object> thisMap = entry.getValue() instanceof Map<?, ?> ? (Map<String, Object>) entry.getValue() : Collections.<String, Object> emptyMap();
			for (int i = 0; i < titles.size(); i++) {
				String title = titles.get(i);
				Object thisValue = title == keyName ? key : thisMap.get(title);
				theseValues.add(thisValue);
			}
			values.add(theseValues);
		}
		final int index = titles.indexOf(sortOrder);
		if (index == -1)
			throw new IllegalArgumentException(MessageFormat.format(UtilityConstants.cannotSort, sortOrder, titles));
		Collections.sort(values, new Comparator<List<Object>>() {

			@Override
			public int compare(List<Object> o1, List<Object> o2) {
				Object l = o1.get(index);
				Object r = o2.get(index);
				if (l == null)
					if (r == null)
						return 0;
					else
						return 1;
				else if (r == null)
					return -1;
				else
					return ((Comparable<Object>) l).compareTo(r);
			}
		});
		return new MapAsList(titles, keyIndex, values);

	}

	public static <KV> KV[] toArray(final Map<? extends KV, ? extends KV> map, KV[] baseArray) {
		List<KV> result = new ArrayList<KV>();
		for (Entry<? extends KV, ? extends KV> entry : map.entrySet()) {
			result.add(entry.getKey());
			result.add(entry.getValue());
		}
		return result.toArray(baseArray);
	}

	public static <K, V> MapDiff<K> diffForTests(Map<K, V> map1, Map<K, V> map2) {
		return diffForTests(map1, map2, new Indent());
	}

	public static <K, V> Map<V, List<K>> inverse(Map<K, V> map) {
		Map<V, List<K>> result = Maps.newMap();
		for (Entry<K, V> entry : map.entrySet())
			Maps.addToList(result, entry.getValue(), entry.getKey());
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K, V> MapDiff<K> diffForTests(Map<K, V> map1, Map<K, V> map2, Indent indent) {
		List<K> keysIn1Not2 = Lists.newList();
		List<K> keysIn2Not1 = Lists.newList();
		List<String> valuesDifferent = Lists.newList();
		for (K key : map1.keySet())
			if (!map2.containsKey(key))
				keysIn1Not2.add(key);
		for (K key : map2.keySet())
			if (!map1.containsKey(key))
				keysIn2Not1.add(key);
		for (K key : map1.keySet()) {
			V value1 = map1.get(key);
			V value2 = map2.get(key);
			if (!value1.equals(value2))
				if (value1 instanceof Map && value2 instanceof Map)
					valuesDifferent.add(key.toString() + diffForTests((Map) value1, (Map) value2, indent.indent()));
				else
					valuesDifferent.add(key.toString() + "-> [" + value1 + "]  and  [" + value2 + "]");
		}
		return new MapDiff<K>(keysIn1Not2, keysIn2Not1, valuesDifferent, indent);
	}

	public static <K, V> void assertEquals(Map<K, V> map1, Map<K, V> map2) {
		if (!map1.equals(map2))
			Assert.fail(diffForTests(map1, map2).toString());

	}

	public static <K, V> Map<K, V> makeMap(Object... attributesAndValues) {
		if (attributesAndValues.length % 2 != 0)
			throw new IllegalArgumentException("Illegal size: " + attributesAndValues.length);
		Map<K, V> result = new HashMap<K, V>(attributesAndValues.length / 2);
		return putInto(result, attributesAndValues);
	}

	public static <K, V> Map<K, V> makeMapWithoutNullValues(Object... attributesAndValues) {
		if (attributesAndValues.length % 2 != 0)
			throw new IllegalArgumentException("Illegal size: " + attributesAndValues.length);
		Map<K, V> result = new HashMap<K, V>(attributesAndValues.length / 2);
		return putInto(false, result, attributesAndValues);

	}

	public static <K, V> Map<K, V> makeImmutableMap(Object... attributesAndValues) {
		Map<K, V> result = new HashMap<K, V>(attributesAndValues.length / 2);
		return Collections.unmodifiableMap(putInto(result, attributesAndValues));
	}

	public static <K, V> Map<K, V> makeLinkedMap(Object... attributesAndValues) {
		Map<K, V> result = new LinkedHashMap<K, V>(attributesAndValues.length / 2);
		return putInto(result, attributesAndValues);
	}

	public static <K, V> Map<K, V> makeLinkedMapFromArray(IFunction1<K, V> fn, K... keys) {
		Map<K, V> result = new LinkedHashMap<K, V>(keys.length);
		for (K key : keys)
			result.put(key, Functions.call(fn, key));
		return result;
	}

	public static <K, V> Map<K, V> merge(Map<K, V>... maps) {
		Map<K, V> result = new HashMap<K, V>();
		for (Map<K, V> map : maps)
			if (map != null)
				result.putAll(map);
		return result;
	}

	public static <K, V> Map<K, V> merge(Iterable<Map<K, V>> maps) {
		Map<K, V> result = new HashMap<K, V>();
		for (Map<K, V> map : maps)
			result.putAll(map);
		return result;
	}

	public static <K1, K2, V> void addToMapOfLinkedMaps(Map<K1, Map<K2, V>> map, K1 key1, K2 key2, V value) {
		addToMapOfMaps(map, LinkedHashMap.class, key1, key2, value);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K1, K2, V> void addToMapOfMaps(Map<K1, Map<K2, V>> map, Class<? extends Map> nestedMapClass, K1 key1, K2 key2, V value) {
		try {
			Map<K2, V> map1 = map.get(key1);
			if (map1 == null) {
				map1 = nestedMapClass.newInstance();
				map.put(key1, map1);
			}
			map1.put(key2, value);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K1, K2, K3, V> void addToMapOfMapOfMaps(Map<K1, Map<K2, Map<K3, V>>> map, Class<? extends Map> nestedMapClass, K1 key1, K2 key2, K3 key3, V value) {
		try {
			Map<K2, Map<K3, V>> map1 = map.get(key1);
			if (map1 == null) {
				map1 = nestedMapClass.newInstance();
				map.put(key1, map1);
			}
			Map<K3, V> map2 = map1.get(key2);
			if (map2 == null) {
				map2 = nestedMapClass.newInstance();
				map1.put(key2, map2);
			}
			map2.put(key3, value);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <K, V> void addToList(Map<K, List<V>> map, K key, V value) {
		Maps.addToCollection(map, ArrayList.class, key, value);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V, ColV extends Collection<V>> void addToCollection(Map<K, ColV> map, Class colClass, K key, V value) {
		try {
			ColV existing = map.get(key);
			if (existing == null) {
				existing = (ColV) colClass.getConstructor().newInstance();
				map.put(key, existing);
			}
			existing.add(value);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private static <K, V> Map<K, V> putInto( Map<K, V> result, Object... attributesAndValues) {
		return putInto(true, result, attributesAndValues);
	}

	@SuppressWarnings("unchecked")
	private static <K, V> Map<K, V> putInto(boolean acceptNullValues, Map<K, V> result, Object... attributesAndValues) {
		List<Object>[] keysAndValues = Lists.partition(2, Arrays.asList(attributesAndValues));
		Iterator<Object> keys = keysAndValues[0].iterator();
		Iterator<Object> values = keysAndValues[1].iterator();
		while (keys.hasNext() && values.hasNext()) {
			K key = (K) keys.next();
			V value = (V) values.next();
			if (result.containsKey(key))
				throw new IllegalArgumentException(MessageFormat.format(UtilityMessages.duplicateKey, key, result.get(key), value));
			if (acceptNullValues || value != null)
				result.put(key, value);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static <K, V> Map<Class, Map<K, V>> partitionByValueClass(Map<K, V> input, Class<? extends Map> nestedMapClass, Class... partitionClasses) {
		List<Class> classList = Arrays.asList(partitionClasses);
		Map<Class, Map<K, V>> result = Maps.newMap();
		for (Entry<K, V> entry : input.entrySet()) {
			V value = entry.getValue();
			int index = Lists.findClass(classList, value);
			if (index == -1)
				throw new IllegalArgumentException("Key of " + entry.getKey() + " has a Value class of " + value.getClass() + " found, which is not in " + classList);
			Class key = classList.get(index);
			Maps.addToMapOfMaps(result, nestedMapClass, key, entry.getKey(), value);
		}
		return result;
	}

	public static <K, V, P> Map<P, Map<K, V>> partitionByKey(Map<K, V> input, IFunction1<K, P> partitionFunction) {
		try {
			Map<P, Map<K, V>> result = Maps.newMap();
			for (Entry<K, V> entry : input.entrySet()) {
				K key = entry.getKey();
				P partition = partitionFunction.apply(key);
				V value = entry.getValue();
				Maps.addToMapOfLinkedMaps(result, partition, key, value);
			}
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <K, V, P> Map<P, Map<K, V>> partitionByValue(Map<K, V> input, IFunction1<V, P> partitionFunction) {
		try {
			Map<P, Map<K, V>> result = Maps.newMap();
			for (Entry<K, V> entry : input.entrySet()) {
				K key = entry.getKey();
				V value = entry.getValue();
				P partition = partitionFunction.apply(value);
				Maps.addToMapOfLinkedMaps(result, partition, key, value);
			}
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <K, V> Map<K, V> newMap() {
		return new HashMap<K, V>();
	}

	public static <K, V> IFunction1<K, V> get(final Map<K, V> map) {
		return new IFunction1<K, V>() {
			@Override
			public V apply(K from) throws Exception {
				return map.get(from);
			}
		};
	}

	public static <K, V> IFunction1<Entry<String, String>, String> entryToStr(final String pattern) {
		return new IFunction1<Map.Entry<String, String>, String>() {
			@Override
			public String apply(Entry<String, String> from) throws Exception {
				return MessageFormat.format(pattern, from.getKey(), from.getValue());
			}
		};
	}

	/** The map is from K to a pattern. The parameters passed to MessageFormat are key + extraParameters */
	public static <K, V> IFunction1<K, String> keyToValuePatternToStr(final Map<K, V> map, final Object... extraParameters) {
		return new IFunction1<K, String>() {
			@Override
			public String apply(K from) throws Exception {
				List<Object> arguments = new ArrayList<Object>();
				arguments.add(from);
				arguments.addAll(Arrays.asList(extraParameters));
				String pattern = map.get(from).toString();
				return MessageFormat.format(pattern, arguments.toArray());
			}
		};
	}

	public static <K, From, To> Map<K, To> mapTheMap(Map<K, From> map, IFunction1<From, To> mapValue) {
		return mapTheMap(map, Functions.<K, K> identity(), mapValue);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <FromK, ToK, FromV, ToV> Map<ToK, ToV> mapTheMap(Map<FromK, FromV> map, IFunction1<FromK, ToK> mapKey, IFunction1<FromV, ToV> mapValue) {
		try {
			Map<ToK, ToV> result = (Map) newMapOfSameTypeMap(map);
			for (Entry<FromK, FromV> entry : map.entrySet())
				result.put(mapKey.apply(entry.getKey()), mapValue.apply(entry.getValue()));
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <K, V, To> List<To> map(Map<K, V> map, IFunction1<Map.Entry<K, V>, To> convertor) {
		try {

			List<To> result = new ArrayList<To>(map.size());
			for (Entry<K, V> entry : map.entrySet())
				result.add(convertor.apply(entry));
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <K, V> Map<K, V> fromSimpleMap(ISimpleMap<K, V> map) {
		Map<K, V> result = new HashMap<K, V>();
		for (K key : map.keys())
			result.put(key, map.get(key));
		return result;
	}

	public static <K, V> V findOrCreate(Map<K, V> map, K key, Callable<V> callable) {
		try {
			if (map.containsKey(key))
				return map.get(key);
			V v = callable.call();
			map.put(key, v);
			return v;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <K> void add(Map<K, Integer> map, K key, int size) {
		map.put(key, size + intFor(map, key));
	}

	public static <K> int intFor(Map<K, Integer> map, K key) {
		Integer existing = map.get(key);
		return existing == null ? 0 : existing;
	}

	public static <T> boolean booleanFor(Map<T, Boolean> map, T t, boolean defaultValue) {
		Boolean value = map.get(t);
		if (value == null)
			return defaultValue;
		else
			return value;
	}

	public static <K, V> Map<K, V> with(Map<K, V> map, Object... keysAndValues) {
		Map<K, V> newMap = copyMap(map);
		newMap.putAll(Maps.<K, V> makeLinkedMap(keysAndValues));
		return newMap;
	}

	public static <K, V> Map<K, V> copyMap(Map<K, V> map) {
		if (map == null)
			return newMap();
		Map<K, V> newMap = newMapOfSameTypeMap(map);
		newMap.putAll(map);
		return newMap;
	}

	public static <K, V> Map<K, V> newMapOfSameTypeMap(Map<K, V> map) {
		return newMap(map.getClass());
	}

	public static <K, V> Map<K, V> newMap(Class<?> clazz) {
		if (LinkedHashMap.class.isAssignableFrom(clazz))
			return new LinkedHashMap<K, V>();
		else if (HashMap.class.isAssignableFrom(clazz))
			return new HashMap<K, V>();
		else if (IdentityHashMap.class.isAssignableFrom(clazz))
			return new IdentityHashMap<K, V>();
		else
			return new HashMap<K, V>();
	}

	@SuppressWarnings("unchecked")
	public static <K, V> V get(Map<K, V> data, K... keys) {
		Map<K, V> map = data;
		V result = null;
		for (K key : keys) {
			if (map == null)
				throw new IllegalStateException(MessageFormat.format(UtilityMessages.cannotWorkOutValue, data, key, Arrays.asList(keys)));
			result = map.get(key);
			if (result instanceof Map)
				map = (Map<K, V>) result;
			else
				map = null;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> void put(Map<K, V> data, K[] keys, V value) {
		Map<K, V> map = data;
		for (int i = 0; i < keys.length - 1; i++) {
			if (map == null)
				break;
			K key = keys[i];
			V result = map.get(key);
			if (result instanceof Map)
				map = (Map<K, V>) result;
			else
				map = null;
		}
		if (map == null)
			throw new IllegalStateException(MessageFormat.format(UtilityMessages.cannotFindMapForGet, data, Arrays.asList(keys)));
		map.put(keys[keys.length - 1], value);

	}

	@SuppressWarnings("unchecked")
	public static <K, V, To> To[] toParameters(Map<K, V> map, IFunction1<K, To> keyConvertor, IFunction1<V, To> valueConvertor, Class<To> toClass) {
		try {
			To[] result = (To[]) Array.newInstance(toClass, 2 * map.size());
			int i = 0;
			for (Entry<K, V> entry : map.entrySet()) {
				result[i++] = keyConvertor.apply(entry.getKey());
				result[i++] = valueConvertor.apply(entry.getValue());
			}
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	public static <K, V> V getOrException(Map<K, V> map, K key) {
		V result = map.get(key);
		if (result == null)
			if (!map.containsKey(key))
				throw new IllegalArgumentException(MessageFormat.format(UtilityConstants.mapDoesntHaveKey, key, map.keySet(), map));
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> List<V> getOrEmptyList(Map<K, List<V>> map, K key) {
		List<V> result = map.get(key);
		if (result == null)
			return Collections.EMPTY_LIST;
		else
			return result;
	}

	public static <K, V> void putIfNotNull(Map<K, V> map, K key, V value) {
		if (value != null)
			map.put(key, value);

	}

	public static <K, V> Map<K, V> copyWithout(Map<K, V> map, K... keys) {
		Map<K, V> result = Maps.copyMap(map);
		for (K key : keys)
			result.remove(key);
		return result;
	}

	public static <K, V> Map<K, V> sortByKey(Map<K, V> map, Comparator<K> comparator) {
		List<K> sortedKeys = Lists.sort(map.keySet(), comparator);
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (K key : sortedKeys)
			result.put(key, map.get(key));
		return result;
	}

	public static Map<String, Object> stringObjectMap(Object... attributesAndValues) {
		return makeMap(attributesAndValues);
	}

	public static Map<String, Object> stringObjectLinkedMap(Object... attributesAndValues) {
		return makeLinkedMap(attributesAndValues);
	}

	public static Map<String, Object> emptyStringObjectMap() {
		return Collections.emptyMap();
	}

	public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
		if (map != null && map.containsKey(key))
			return map.get(key);
		else
			return defaultValue;
	}

	public static <K, V> IFunction1<Map<K, V>, Map<K, V>> withFn(final K newKey, final V newValue) {
		return new IFunction1<Map<K, V>, Map<K, V>>() {
			@Override
			public Map<K, V> apply(Map<K, V> from) throws Exception {
				return with(from, newKey, newValue);
			}
		};
	}

}