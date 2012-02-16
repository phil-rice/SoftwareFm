/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.jdtBinding;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.softwareFm.eclipse.jdtBinding.mocks.IBindingBuilder;

/**
 * @author Phil
 * 
 */
public class BindingBuilderTest extends TestCase {
	private final String pathname = "/DataGenerator/src/main/java/org/fastorm/dataGenerator/api/IGenerator.java";
	private final File path = new File(pathname);

	public void testClassMethod() {
		IBinding binding = IBindingBuilder.Utils.//
				parent(pathname, getClass()).//
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
				parent(pathname, getClass()).//
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
				parent(pathname, getClass()).//
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