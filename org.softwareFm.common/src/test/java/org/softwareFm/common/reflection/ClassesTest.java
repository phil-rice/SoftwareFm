/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.reflection;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.reflection.Classes;
import org.softwareFm.common.reflection.IClassAcceptor;
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