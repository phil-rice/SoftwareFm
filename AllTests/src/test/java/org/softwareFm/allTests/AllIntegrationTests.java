package org.softwareFm.allTests;

import java.io.File;
import java.lang.reflect.Modifier;

import junit.framework.Test;

import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.reflection.IClassAcceptor;
import org.softwareFm.utilities.tests.IIntegrationTest;
import org.softwareFm.utilities.tests.Tests;

public class AllIntegrationTests {

	public static Test suite() {
		return Tests.makeSuiteUnder(AllIntegrationTests.class, new File(".."), new IClassAcceptor() {
			@Override
			public boolean accept(File file, Class<?> clazz) {
				if (!IIntegrationTest.class.isAssignableFrom(clazz))
					return false;
				if (Modifier.isAbstract(clazz.getModifiers()))
					return false;
				String name = Files.noExtension(file.getName());
				boolean result = name.endsWith("Test");
				return result;
			}
		});
	}
}
