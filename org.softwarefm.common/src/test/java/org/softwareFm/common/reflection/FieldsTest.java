/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.reflection;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.springframework.core.io.ClassPathResource;

public class FieldsTest extends TestCase {

	public int publicIntField;
	public static int publicStaticIntField = 1;
	public static final int publicStaticFinalIntField = 2;
	public static final Integer publicStaticFinalIntegerField = 3;
	@SuppressWarnings("unused")
	private static final int privateStaticFinalIntField = 4;

	public static final String a = "one";
	public static final String b = "two";
	public String c = "three";
	static String d = "four";
	static final String e = "five";

	public void testFields() {
		assertEquals(Arrays.asList("publicIntField", "publicStaticIntField", "publicStaticFinalIntField", "publicStaticFinalIntegerField", "a", "b", "c"), Iterables.list(Fields.names(Fields.publicFields(getClass()))));
	}

	public void testConstants() {
		checkConstants(getClass(), "publicStaticFinalIntField", "publicStaticFinalIntegerField", "a", "b");
		checkConstants(TestClassWithFields.class, "f", "g");
		checkConstants(ClassesTest.class);
	}

	private void checkConstants(Class<?> class1, String... expected) {
		assertEquals(Arrays.asList(expected), Iterables.list(Fields.names(Fields.constants(class1))));
	}

	public void testConstantFieldsOfClass() {
		checkConstantFieldsOfClass(String.class, "a", "b");
		checkConstantFieldsOfClass(int.class, "publicStaticFinalIntField");
		checkConstantFieldsOfClass(File.class);
		checkConstantFieldsOfClass(Object.class, "publicStaticFinalIntegerField", "a", "b");
	}

	private void checkConstantFieldsOfClass(Class<?> clazz, String... expected) {
		assertEquals(Arrays.asList(expected), Iterables.list(Fields.names(Fields.constantFieldsOfClass(getClass(), clazz))));
	}

	public void testConstantsOfClass() {
		checkConstantsOfClass(String.class, "one", "two");
		checkConstantsOfClass(int.class, 2);
		checkConstantsOfClass(Integer.class, 3);
		checkConstantsOfClass(File.class);
		checkConstantsOfClass(Object.class, 3, "one", "two");
	}

	private void checkConstantsOfClass(Class<?> clazz, Object... expected) {
		assertEquals(Arrays.asList(expected), Iterables.list(Fields.constantsOfClass(getClass(), clazz)));
	}

	public void testConstantsOfClassWithFilter() {
		checkConstantsOfClassWithFilter(String.class, Fields.nameStartWith("a"), "one");
		checkConstantsOfClassWithFilter(String.class, Fields.nameStartWith("b"), "two");
		checkConstantsOfClassWithFilter(String.class, Functions.<Field> trueFn(), "one", "two");
		checkConstantsOfClassWithFilter(String.class, Functions.<Field> falseFn());
	}

	private void checkConstantsOfClassWithFilter(Class<?> clazz, IFunction1<Field, Boolean> filter, Object... expected) {
		assertEquals(Arrays.asList(expected), Iterables.list(Fields.constantsOfClassWithFilter(getClass(), clazz, filter)));
	}

	public void testConstantsOfClassInDirectory() {
		checkConstantsOfClassInDirectory(String.class, "one", "two", "six", "seven");
		checkConstantsOfClassInDirectory(Integer.class, 3);
		checkConstantsOfClassInDirectory(int.class, 2);
		checkConstantsOfClassInDirectory(File.class);
	}

	private void checkConstantsOfClassInDirectory(@SuppressWarnings("rawtypes") Class class1, Object... expected) {
		@SuppressWarnings("unchecked")
		List<?> actual = Iterables.list(Fields.constantsOfClassInDirectory(new ClassPathResource(".", getClass()), getClass().getPackage().getName(), class1));
		assertEquals(Arrays.asList(expected), actual);
	}

	public void testConstantsOfClassInDirectoryWithFilter() {
		IFunction1<Field, Boolean> lessThanC = new IFunction1<Field, Boolean>() {
			@Override
			public Boolean apply(Field from) throws Exception {
				return from.getName().compareTo("c") < 0;
			}
		};
		checkConstantsOfClassInDirectoryWithFilter(String.class, Fields.nameStartWith("a"), "one");
		checkConstantsOfClassInDirectoryWithFilter(String.class, lessThanC, "one", "two");
		checkConstantsOfClassInDirectoryWithFilter(Integer.class, Functions.<Field> trueFn(), 3);
		checkConstantsOfClassInDirectoryWithFilter(Integer.class, Functions.<Field> falseFn());
		checkConstantsOfClassInDirectoryWithFilter(int.class, Functions.<Field> trueFn(), 2);
		checkConstantsOfClassInDirectoryWithFilter(int.class, Functions.<Field> falseFn());
	}

	private <T> void checkConstantsOfClassInDirectoryWithFilter(Class<T> typeClass, IFunction1<Field, Boolean> filter, Object... expected) {
		List<?> actual = Iterables.list(Fields.constantsOfClassInDirectoryWithFilter(new ClassPathResource(".", getClass()), getClass().getPackage().getName(), typeClass, filter));
		assertEquals(Arrays.asList(expected), actual);

	}

	public void testConstantsOfClassa() {
		assertEquals(Arrays.asList("one", "two"), Iterables.list(Fields.constantsOfClass(getClass(), String.class)));
		assertEquals(Arrays.asList("one", "two", "six", "seven"), Iterables.list(Fields.constantsOfClassInDirectory(new ClassPathResource(".", getClass()), getClass().getPackage().getName(), String.class)));
	}
}