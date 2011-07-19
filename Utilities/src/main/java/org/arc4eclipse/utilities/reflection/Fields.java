package org.arc4eclipse.utilities.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.arc4eclipse.utilities.collections.Iterables;
import org.arc4eclipse.utilities.functions.IFunction1;

public class Fields {

	public static Iterable<Field> publicFields(Class<?> clazz) {
		Field[] raw = clazz.getFields();
		return Iterables.iterable(raw);
	}

	public static Iterable<Field> constants(Class<?> clazz) {
		return Iterables.filter(publicFields(clazz), new IFunction1<Field, Boolean>() {
			@Override
			public Boolean apply(Field from) throws Exception {
				int mod = from.getModifiers();
				return Modifier.isFinal(mod) && Modifier.isStatic(mod);
			}
		});
	}

	public static Iterable<Field> constantFieldsOfClass(Class<?> dataClass, final Class<?> typeClass) {
		return Iterables.filter(publicFields(dataClass), new IFunction1<Field, Boolean>() {
			@Override
			public Boolean apply(Field from) throws Exception {
				int mod = from.getModifiers();
				boolean assignable = typeClass.isAssignableFrom(from.getType());
				boolean modifiers = Modifier.isFinal(mod) && Modifier.isStatic(mod);
				return modifiers && assignable;
			}
		});
	}

	public static <T> Iterable<T> constantsOfClass(Class<?> dataClass, final Class<T> typeClass) {
		IFunction1<Field, Boolean> acceptor = new IFunction1<Field, Boolean>() {
			@Override
			public Boolean apply(Field from) throws Exception {
				int mod = from.getModifiers();
				boolean assignable = typeClass.isAssignableFrom(from.getType());
				boolean modifiers = Modifier.isFinal(mod) && Modifier.isStatic(mod);
				return modifiers && assignable;
			}
		};
		IFunction1<Field, T> mapper = new IFunction1<Field, T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T apply(Field from) throws Exception {
				return (T) from.get(null);
			}
		};
		return Iterables.map(Iterables.filter(publicFields(dataClass), acceptor), mapper);
	}

	public final static IFunction1<Field, String> fieldToName = new IFunction1<Field, String>() {
		@Override
		public String apply(Field from) throws Exception {
			return from.getName();
		}
	};

	public static Iterable<String> names(Iterable<Field> fields) {
		return Iterables.map(fields, fieldToName);
	}

}
