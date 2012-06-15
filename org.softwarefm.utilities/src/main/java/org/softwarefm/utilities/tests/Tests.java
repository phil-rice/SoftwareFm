/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.tests;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.softwarefm.utilities.collections.Files;
import org.softwarefm.utilities.collections.Iterables;
import org.softwarefm.utilities.collections.Sets;
import org.softwarefm.utilities.constants.CommonConstants;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.functions.Functions;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.reflection.Classes;
import org.softwarefm.utilities.reflection.Fields;
import org.softwarefm.utilities.reflection.IClassAcceptor;

public class Tests {

	public static <T> T assertNotNull(T t) {
		Assert.assertNotNull(t);
		return t;
	}

	public static void waitUntil(Callable<Boolean> callable) {
		long startTime = System.currentTimeMillis();
		try {
			while (!callable.call() && System.currentTimeMillis() < startTime + CommonConstants.testTimeOutMs)
				Thread.sleep(1);
			if (!callable.call())
				Assert.fail();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	public static boolean deleteTempDirectory(String name) {
		String tempDir = System.getProperty("java.io.tmpdir");
		File tests = new File(tempDir, "softwareFmTests");
		File file = new File(tests, name);
		Files.deleteDirectory(file);
		return !file.exists();
	}

	public static void waitUntilCanDeleteTempDirectory(String name, long maxWaitTime) {
		long startTime = System.currentTimeMillis();
		boolean deleted;
		while (!(deleted = deleteTempDirectory(name)) && System.currentTimeMillis() < startTime + maxWaitTime)
			doNothing();
		if (!deleted)
			throw new RuntimeException(name);

	}

	private static void doNothing() {
	}

	public static File makeTempDirectory(String name) {
		String tempDir = System.getProperty("java.io.tmpdir");
		Assert.assertTrue(!tempDir.equals(""));
		File tests = new File(tempDir, "softwareFmTests");
		tests.mkdirs();
		File result = new File(tests, name);
		waitUntilCanDeleteTempDirectory(name, 3000);
		boolean exists = result.exists();
		Assert.assertFalse("Cannot delete temp file for: " + name, exists);
		return result;
	}

	public static <E extends Throwable> E assertThrowsWithMessage(String message, Class<E> class1, Runnable runnable) {
		E e = assertThrows(class1, runnable);
		Assert.assertEquals(message, e.getMessage());
		return e;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> E assertThrows(Class<E> class1, Runnable runnable) {
		try {
			runnable.run();
			Assert.fail(class1.getSimpleName() + " not thrown");
		} catch (Throwable e) {
			if (e instanceof WrappedException && !WrappedException.class.isAssignableFrom(class1))
				e = e.getCause();
			if (class1.isAssignableFrom(e.getClass()))
				return (E) e;
			Assert.fail(e.toString());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> E assertThrows(boolean expected, Class<E> clazz, Runnable runnable) {
		try {
			runnable.run();
			if (!expected)
				Assert.fail();
			return null;
		} catch (Throwable e) {
			if (e.getClass() == clazz)
				if (expected)
					Assert.fail();
				else
					return (E) e;
			throw WrappedException.wrap(e);
		}
	}

	public static TestSuite makeSuiteUnder(Class<?> marker, File root, IClassAcceptor acceptor) {
		Class<?>[] array = Iterables.list(testsUnder(root, acceptor)).toArray(new Class[0]);
		TestSuite suite = new TestSuite(array);
		return suite;
	}

	public static Iterable<Class<?>> testsUnder(File root, IClassAcceptor acceptor) {
		Iterable<File> children = Files.walkChildrenOf(root, Files.extensionFilter("class"));
		// System.out.println(Iterables.list(children));
		// System.out.println();
		Iterable<Class<?>> classes = Iterables.map(children, Classes.asClass(acceptor));
		// System.out.println(Iterables.list(classes));
		Iterable<Class<?>> notNullClasses = Iterables.remove(classes, Functions.<Class<?>> isNull());
		Iterable<Class<?>> result = Iterables.remove(notNullClasses, new IFunction1<Class<?>, Boolean>() {

			public Boolean apply(Class<?> from) throws Exception {
				return IDontRunAutomaticallyTest.class.isAssignableFrom(from);
			}
		});
		// System.out.println();
		// System.out.println(Iterables.list(result));
		return result;
	}

	public static void main(String[] args) {
		for (Class<?> clazz : testsUnder(new File(".."), IClassAcceptor.Utils.isTest()))
			System.out.println(clazz.getName());
	}

	public static void executeTest(Class<? extends TestCase> class1) {
		TestSuite suite = new TestSuite("Tests");
		suite.addTestSuite(class1);
		new TestRunner().doRun(suite);
	}

	public static <T> void checkEqualityAndHashcode(IFunction1<String, T> creator) {
		try {
			T onea = creator.apply("one");
			T oneb = creator.apply("one");
			T two = creator.apply("two");
			Assert.assertEquals(onea, onea);
			Assert.assertEquals(onea, oneb);
			Assert.assertEquals(onea.hashCode(), oneb.hashCode());
			Assert.assertTrue(onea.hashCode() != two.hashCode());// by some crazy chance this might fail...
			Assert.assertFalse(onea.equals(null));
			Assert.assertFalse(onea.equals(two));
			Assert.assertFalse(two.equals(onea));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}

	public static void checkResourceBundle(Class<?> anchorClass, String bundleName, Class<?> referenceClass) {
		Set<String> javaConstants = Sets.set(Iterables.map(Fields.constantFieldsOfClass(referenceClass, String.class), Fields.<String> constantFieldToValue()));
		ResourceBundle bundle = ResourceBundle.getBundle(anchorClass.getPackage().getName() + "." + bundleName, Locale.getDefault(), anchorClass.getClassLoader());
		for (String constant : javaConstants)
			bundle.getString(constant);
		for (String key : bundle.keySet())
			Assert.assertTrue(key, javaConstants.contains(key));
	}
}