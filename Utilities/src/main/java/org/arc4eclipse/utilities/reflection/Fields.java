package org.arc4eclipse.utilities.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.arc4eclipse.utilities.collections.Iterables;
import org.arc4eclipse.utilities.functions.IFunction1;

public class Fields {

	public static Iterable<Field> publicFields(Class<?> clazz) {
		return Iterables.iterable(clazz.getFields());
	}

	public static Iterable<Field> constants(Class<?> clazz) {
		return Iterables.filter(publicFields(clazz), new IFunction1<Field, Boolean>() {
			public Boolean apply(Field from) throws Exception {
				int mod = from.getModifiers();
				return Modifier.isFinal(mod) && Modifier.isStatic(mod);
			}
		});
	}

	public final static IFunction1<Field, String> fieldToName = new IFunction1<Field, String>() {
		public String apply(Field from) throws Exception {
			return from.getName();
		}
	};

	public static Iterable<String> names(Iterable<Field> fields) {
		return Iterables.map(fields, fieldToName);
	}

}
