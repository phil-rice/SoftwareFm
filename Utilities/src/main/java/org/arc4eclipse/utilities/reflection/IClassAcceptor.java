package org.arc4eclipse.utilities.reflection;

import java.io.File;
import java.lang.reflect.Modifier;

import org.arc4eclipse.utilities.collections.Files;

public interface IClassAcceptor {

	boolean accept(File file, Class<?> clazz);

	public static class Utils {
		public static IClassAcceptor all() {
			return new IClassAcceptor() {
				@Override
				public boolean accept(File file, Class<?> clazz) {
					return true;
				}
			};
		}

		public static IClassAcceptor endsWith(final String text) {
			return new IClassAcceptor() {
				@Override
				public boolean accept(File file, Class<?> clazz) {
					String name = Files.noExtension(file.getName());
					return name.endsWith(text);
				}
			};
		}

		public static IClassAcceptor isTest() {
			return new IClassAcceptor() {
				@Override
				public boolean accept(File file, Class<?> clazz) {
					if (Modifier.isAbstract(clazz.getModifiers()))
						return false;
					String name = Files.noExtension(file.getName());
					boolean result = name.endsWith("Test");
					if (result)
						System.out.println(file + " -------- " + clazz);
					return result;
				}
			};
		}
	}

}
