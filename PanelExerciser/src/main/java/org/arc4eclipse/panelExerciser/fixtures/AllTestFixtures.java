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
