package org.arc4eclipse.utilities.reflection;

import java.util.Arrays;

import junit.framework.TestCase;

import org.arc4eclipse.utilities.collections.Iterables;
import org.springframework.core.io.ClassPathResource;

public class FieldsTest extends TestCase {

	public int intField;
	public static int staticIntField = 0;
	public static final int staticFinalIntField = 0;
	@SuppressWarnings("unused")
	private static final int privateStaticFinalIntField = 0;

	public static final String a = "one";
	public static final String b = "two";
	public String c = "three";
	static String d = "four";
	static final String e = "five";

	public void testFields() {
		assertEquals(Arrays.asList("intField", "staticIntField", "staticFinalIntField", "a", "b", "c"), Iterables.list(Fields.names(Fields.publicFields(getClass()))));
		assertEquals(Arrays.asList("staticFinalIntField", "a", "b"), Iterables.list(Fields.names(Fields.constants(getClass()))));
	}

	public void testConstantsOfClass() {
		assertEquals(Arrays.asList("one", "two"), Iterables.list(Fields.constantsOfClass(getClass(), String.class)));
		assertEquals(Arrays.asList("one", "two", "six", "seven"), Iterables.list(Fields.constantsOfClass(new ClassPathResource(".", getClass()), getClass().getPackage().getName(), String.class)));
	}
}
