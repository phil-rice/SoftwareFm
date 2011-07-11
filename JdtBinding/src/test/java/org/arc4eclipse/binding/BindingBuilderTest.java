package org.arc4eclipse.binding;

import java.io.File;

import junit.framework.TestCase;

import org.arc4eclipse.binding.mocks.IBindingBuilder;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * @author Phil
 * 
 */
public class BindingBuilderTest extends TestCase {
	private final String pathname = "/DataGenerator/src/main/java/org/fastorm/dataGenerator/api/IGenerator.java";
	private final File path = new File(pathname);

	public void testClassMethod() {
		IBinding binding = IBindingBuilder.Utils.//
				parent(pathname).//
				withPackage("org.someOrg.somePackage").//
				withInterface("IGenerator").//
				child().//
				withMethod("someMethod");

		IJavaElement javaElement = binding.getJavaElement();
		IJavaElement parent = javaElement.getParent();

		assertEquals(path, parent.getPath().toFile());
		assertEquals("IGenerator", parent.getElementName());
		assertTrue(parent instanceof IClassFile);

		assertEquals(path, javaElement.getPath().toFile());
		assertEquals("someMethod", javaElement.getElementName());
		assertTrue(javaElement instanceof IMethod);

		assertFalse(binding instanceof ITypeBinding);
		assertTrue(binding instanceof IMethodBinding);
		assertEquals("org.someOrg.somePackage", ((IMethodBinding) binding).getDeclaringClass().getPackage().getName());
	}

	public void testClassInterface() {
		IBinding binding = IBindingBuilder.Utils.//
				parent(pathname).//
				withPackage("org.someOrg.somePackage").//
				withInterface("IGenerator").//
				child().//
				withInterface("someMethod");

		IJavaElement javaElement = binding.getJavaElement();
		IJavaElement parent = javaElement.getParent();

		assertEquals(path, parent.getPath().toFile());
		assertEquals("IGenerator", parent.getElementName());
		assertTrue(parent instanceof IClassFile);

		assertEquals(path, javaElement.getPath().toFile());
		assertEquals("someMethod", javaElement.getElementName());
		assertTrue(javaElement.getClass().toString(), javaElement instanceof IClassFile);

		assertTrue(binding instanceof ITypeBinding);
		assertEquals("org.someOrg.somePackage", ((ITypeBinding) binding).getPackage().getName());
	}

	public void testMethodParameter() {
		IBinding binding = IBindingBuilder.Utils.//
				parent(pathname).//
				withMethod("someMethod").//
				child().//
				withLocalVariable("var");//

		IJavaElement javaElement = binding.getJavaElement();
		IJavaElement parent = javaElement.getParent();

		assertEquals(path, parent.getPath().toFile());
		assertEquals("someMethod", parent.getElementName());
		assertTrue(parent instanceof IMethod);

		assertEquals(path, javaElement.getPath().toFile());
		assertEquals("var", javaElement.getElementName());
		assertTrue(javaElement instanceof ILocalVariable);
		assertFalse(binding instanceof ITypeBinding);
		assertTrue(binding instanceof IMethodBinding);
	}
}
