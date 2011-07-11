package org.arc4eclipse.panelExerciser;

import java.util.Arrays;
import java.util.List;

import org.arc4eclipse.binding.mocks.IBindingBuilder;
import org.eclipse.jdt.core.dom.IBinding;

public class PanelExerciserTestFixture {

	public static final String generatorJar = "/DataGenerator/src/main/java/org/fastorm/dataGenerator/api/IGenerator.jar";

	public static IBinding IGenerator$Utils = IBindingBuilder.Utils.//
			parent(generatorJar).withPackage("org.fastorm.dataGenerator.api").withInterface("IGenerator").//
			child().withClazz("Utils");

	public static IBinding Utils$ColumnName = IBindingBuilder.Utils.//
			parent(generatorJar).withPackage("org.fastorm.dataGenerator.api").withClazz("Utils").//
			child().withMethod("makeNew");

	public static final List<IBinding> bindings = Arrays.asList(IGenerator$Utils, Utils$ColumnName);
}
