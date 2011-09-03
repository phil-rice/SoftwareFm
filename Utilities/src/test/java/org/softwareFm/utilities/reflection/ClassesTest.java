package org.softwareFm.utilities.reflection;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.utilities.functions.IFunction1;
import org.springframework.core.io.ClassPathResource;

public class ClassesTest extends TestCase {

	@Test
	public void testClasses() throws Exception {
		checkClass(IClassAcceptor.Utils.all(), ClassesTest.class, true);
		checkClass(IClassAcceptor.Utils.endsWith("Test"), ClassesTest.class, true);
		checkClass(IClassAcceptor.Utils.endsWith("Fail"), ClassesTest.class, false);
	}

	void checkClass(IClassAcceptor classAcceptor, Class<ClassesTest> clazz, boolean expected) throws Exception, IOException {
		File file = new ClassPathResource(clazz.getSimpleName() + ".class", getClass()).getFile();
		IFunction1<File, Class<?>> asClass = Classes.asClass(classAcceptor);
		Class<?> actual = asClass.apply(file);
		if (expected)
			assertEquals(clazz, actual);
		else
			assertNull(actual);
	}
}
