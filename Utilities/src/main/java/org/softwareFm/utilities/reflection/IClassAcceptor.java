/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.reflection;

import java.io.File;
import java.lang.reflect.Modifier;

import org.softwareFm.utilities.collections.Files;

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