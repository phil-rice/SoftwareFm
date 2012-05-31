/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.arrays;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.softwarefm.utilities.collections.Lists;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.functions.IFunction1;

public class ArrayHelper {

	public static <T> T[] append(T[] raw, T... more) {
		List<T> result = Lists.newList();
		for (T t : raw)
			result.add(t);
		for (T t : more)
			result.add(t);
		return result.toArray(raw);

	}

	public static <T> T[] insert(T[] raw, T... toInsert) {
		List<T> result = Lists.newList();
		for (T t : toInsert)
			result.add(t);
		for (T t : raw)
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

	public static <T> int findIndexOf(T[] array, T item) {
		for (int i = 0; i < array.length; i++)
			if (array[i] == item)
				return i;
		return -1;
	}

	public static List<Integer> asList(int[] data) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < data.length; i++)
			result.add(data[i]);
		return result;
	}

	public static <T> int indentityIndexOf(T[] array, T t) {
		for (int i = 0; i < array.length; i++)
			if (array[i] == t)
				return i;
		return -1;
	}

}