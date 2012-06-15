/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.reflection;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.functions.Functions;
import org.softwarefm.utilities.functions.IFunction1;

//TODO rewrite this as a DSL for querying fields and constants.
public class Fields {

	public static Iterable<Field> publicFields(Class<?> clazz) {
		Field[] raw = clazz.getFields();
		return Iterables.iterable(raw);
	}

	public static Iterable<Field> constants(Class<?> clazz) {
		return Iterables.filter(publicFields(clazz), new IFunction1<Field, Boolean>() {
			public Boolean apply(Field from) throws Exception {
				int mod = from.getModifiers();
				return Modifier.isFinal(mod) && Modifier.isStatic(mod);
			}
		});
	}

	public static Iterable<Field> constantFieldsOfClass(Class<?> dataClass, final Class<?> typeClass) {
		return Iterables.filter(publicFields(dataClass), new IFunction1<Field, Boolean>() {
			public Boolean apply(Field from) throws Exception {
				int mod = from.getModifiers();
				boolean assignable = typeClass.isAssignableFrom(from.getType());
				boolean modifiers = Modifier.isFinal(mod) && Modifier.isStatic(mod);
				return modifiers && assignable;
			}
		});
	}

	public final static IFunction1<Field, Boolean> isConstant = new IFunction1<Field, Boolean>() {
		public Boolean apply(Field from) throws Exception {
			int mod = from.getModifiers();
			return Modifier.isFinal(mod) && Modifier.isStatic(mod);
		}
	};

	public final static IFunction1<Field, Boolean> isConstantOfType(final Class<?> typeClass) {
		return new IFunction1<Field, Boolean>() {
			public Boolean apply(Field from) throws Exception {
				int mod = from.getModifiers();
				boolean assignable = typeClass.isAssignableFrom(from.getType());
				boolean modifiers = Modifier.isFinal(mod) && Modifier.isStatic(mod);
				return modifiers && assignable;
			}
		};
	}

	public final static IFunction1<Field, Boolean> nameStartWith(final String prefix) {
		return new IFunction1<Field, Boolean>() {
			public Boolean apply(Field from) throws Exception {
				return from.getName().startsWith(prefix);
			}
		};
	}

	public static <T> Iterable<T> constantsOfClass(Class<?> dataClass, final Class<T> typeClass) {
		return fieldsOfClass(dataClass, typeClass, isConstantOfType(typeClass));
	}

	public static <T> Iterable<T> constantsOfClassWithFilter(Class<?> dataClass, Class<T> typeClazz, IFunction1<Field, Boolean> filter) {
		@SuppressWarnings("unchecked")
		IFunction1<Field, Boolean> acceptor = Functions.<Field> and(isConstantOfType(typeClazz), filter);
		return fieldsOfClass(dataClass, typeClazz, acceptor);
	}

	public static <T> Iterable<T> fieldsOfClass(Class<?> dataClass, final Class<T> typeClass, IFunction1<Field, Boolean> acceptor) {
		IFunction1<Field, T> mapper = new IFunction1<Field, T>() {
			@SuppressWarnings("unchecked")
			public T apply(Field from) throws Exception {
				return (T) from.get(null);
			}
		};
		return Iterables.map(Iterables.filter(publicFields(dataClass), acceptor), mapper);
	}

	public static Class<?> fileToClass(String packageName, File file) {
		try {
			String name = Files.justName(file);
			Class<?> clazz = Class.forName(packageName + "." + name);
			return clazz;
		} catch (Exception e) {
			throw new RuntimeException("Class from file failed: " + file, e);
		}
	}

	public final static IFunction1<Field, String> fieldToName = new IFunction1<Field, String>() {
		public String apply(Field from) throws Exception {
			return from.getName();
		}
	};

	public final static <T> IFunction1<Field, T> constantFieldToValue() {
		return new IFunction1<Field, T>() {
			@SuppressWarnings("unchecked")
			public T apply(Field from) throws Exception {
				return (T) from.get(null);
			}
		};
	}

	public static Iterable<String> names(Iterable<Field> fields) {
		return Iterables.map(fields, fieldToName);
	}

}