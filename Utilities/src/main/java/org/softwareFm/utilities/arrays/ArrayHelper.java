package org.softwareFm.utilities.arrays;

import java.lang.reflect.Array;
import java.util.List;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;

public class ArrayHelper {

	public static <T> T[] append(T[] raw, T... more) {
		List<T> result = Lists.newList();
		for (T t : raw)
			result.add(t);
		for (T t : more)
			result.add(t);
		return result.toArray(raw);

	}

	public static <From, To> To[] map(Class<To> clazz, From[] old, IFunction1<From, To> mutator) {
		try {
			@SuppressWarnings({ "unchecked" })
			To[] result = (To[]) Array.newInstance(clazz, old.length);
			for (int i = 0; i < old.length; i++)
				result[i] = mutator.apply(old[i]);
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static <T>int findIndexOf(T[] array, T item) {
		for (int i = 0; i<array.length; i++)
			if (array[i] == item)
				return i;
		return -1;
	}
}
