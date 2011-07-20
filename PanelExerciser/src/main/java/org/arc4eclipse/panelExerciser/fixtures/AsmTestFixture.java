package org.arc4eclipse.panelExerciser.fixtures;

import org.arc4eclipse.binding.mocks.IBindingBuilder;
import org.eclipse.jdt.core.dom.IBinding;

//This is a jar that doesn't have any data about it in the repository
public class AsmTestFixture {

	public static final String asmJar = "../PanelExerciser/src/main/resources/asm-all-3.1.jar";

	public final static IBinding Method_AppenderSkeleton_doAppend = IBindingBuilder.Utils.//
			parent(asmJar).withPackage("org.objectweb.asm").withClass("ClassReader").//
			child().withMethod("getAccess");

}
