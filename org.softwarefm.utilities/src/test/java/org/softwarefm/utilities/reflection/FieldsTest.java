/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.reflection;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

import junit.framework.TestCase;

import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.functions.Functions;
import org.softwarefm.utilities.functions.IFunction1;

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

}