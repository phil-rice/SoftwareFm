/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.pooling;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.List;

import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.maps.ArraySimpleMap;
import org.softwareFm.utilities.strings.ByteArraySimpleString;
import org.softwareFm.utilities.strings.ByteBufferSimpleString;
import org.softwareFm.utilities.strings.ISimpleStringWithSetters;

public interface IObjectDefinition<T> {

	Class<T> objectClass();

	T createBlank();

	void clean(T oldObject);

	static class Utils {
		public static IObjectDefinition<ISimpleStringWithSetters> arraySimpleStringDefn(final int maxStringLength) {
			return new IObjectDefinition<ISimpleStringWithSetters>() {
				public Class<ISimpleStringWithSetters> objectClass() {
					return ISimpleStringWithSetters.class;
				}

				public ISimpleStringWithSetters createBlank() {
					return new ByteArraySimpleString(new byte[maxStringLength]);
				}

				public void clean(ISimpleStringWithSetters oldObject) {
					oldObject.setFromString("");
				}
			};
		}

		public static IObjectDefinition<ISimpleStringWithSetters> bufferSimpleStringDefn(final int maxStringLength) {
			return new IObjectDefinition<ISimpleStringWithSetters>() {
				public Class<ISimpleStringWithSetters> objectClass() {
					return ISimpleStringWithSetters.class;
				}

				public ISimpleStringWithSetters createBlank() {
					return new ByteBufferSimpleString(ByteBuffer.allocate(maxStringLength));
				}

				public void clean(ISimpleStringWithSetters oldObject) {
					oldObject.setFromString("");
				}
			};
		}

		@SuppressWarnings("unchecked")
		public static <T> IObjectDefinition<T[]> arrayDefn(final Class<T> clazz, final int arrayLength) {
			final Class<T[]> arrayClass = (Class<T[]>) Array.newInstance(clazz, 0).getClass();
			return new IObjectDefinition<T[]>() {
				public Class<T[]> objectClass() {
					return arrayClass;
				}

				public T[] createBlank() {
					return (T[]) Array.newInstance(clazz, arrayLength);
				}

				public void clean(T[] oldObject) {
					for (int i = 0; i < oldObject.length; i++)
						oldObject[i] = null;
				}
			};
		}

		@SuppressWarnings("unchecked")
		public static <K, V> IObjectDefinition<ArraySimpleMap<K, V>> arraySimpleMapDefn(final List<K> keys, final Class<V> valueClass) {
			return arraySimpleMapDefn(keys, ArraySimpleMap.class, valueClass);
		}

		public static <K, V, T extends ArraySimpleMap<K, V>> IObjectDefinition<T> arraySimpleMapDefn(final List<K> keys, final Class<T> resultClass, final Class<V> valueClass) {
			return new IObjectDefinition<T>() {
				public Class<T> objectClass() {
					return resultClass;
				}

				public T createBlank() {
					try {
						Constructor<T> constructor = resultClass.getConstructor(new Class[] { List.class, Class.class });
						T map = constructor.newInstance(keys, valueClass);
						return map;
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}

				public void clean(T oldObject) {
					V[] values = oldObject.getValues();
					for (int i = 0; i < values.length; i++)
						values[i] = null;
				}
			};
		}
	}
}