/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.arc4eclipse.panelExerciser.fixtures;

import java.util.Map;

import org.arc4eclipse.panelExerciser.JarDataAndPath;
import org.arc4eclipse.utilities.collections.Iterables;
import org.arc4eclipse.utilities.reflection.Fields;
import org.arc4eclipse.utilities.strings.Strings;
import org.eclipse.jdt.core.dom.IBinding;
import org.springframework.core.io.ClassPathResource;

public class AllTestFixtures {

	private final static ClassPathResource directory = new ClassPathResource(".", AllTestFixtures.class);
	private final static String packageName = AllTestFixtures.class.getPackage().getName();

	public static Iterable<IBinding> allBindings() {
		return Fields.constantsOfClassInDirectory(directory, packageName, IBinding.class);
	}

	public static Iterable<JarDataAndPath> allJarDataConstants() {
		return Fields.constantsOfClassInDirectory(directory, packageName, JarDataAndPath.class);
	}

	public static Iterable<Map<String, Object>> allOrganisationDataConstants() {
		return Fields.constantsOfClassInDirectoryWithFilter(directory, packageName, Map.class, Fields.nameStartWith("org"));
	}

	public static Iterable<Map<String, Object>> allProjectDataConstants() {
		return Fields.constantsOfClassInDirectoryWithFilter(directory, packageName, Map.class, Fields.nameStartWith("proj"));
	}

	public static void main(String[] args) {
		System.out.println("Jars:");
		System.out.println(Iterables.aggregate(allJarDataConstants(), Strings.<JarDataAndPath> join("\n")));
		System.out.println("Organisation:");
		System.out.println(Iterables.aggregate(allOrganisationDataConstants(), Strings.<Map<String, Object>> join("\n")));
		System.out.println("Project:");
		System.out.println(Iterables.aggregate(allProjectDataConstants(), Strings.<Map<String, Object>> join("\n")));
	}
}