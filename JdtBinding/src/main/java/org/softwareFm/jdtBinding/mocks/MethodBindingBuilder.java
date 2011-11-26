/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jdtBinding.mocks;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class MethodBindingBuilder extends BindingBuilder implements IMethodBinding {

	protected MethodBindingBuilder(Path path, String packageName, boolean modifyingParent, JavaElementData parentData, JavaElementData childData) {
		super(path, packageName, modifyingParent, parentData, childData);
	}

	protected IBindingBuilder newBuilder(JavaElementData data) {
		return newMethodBuilder(data);
	}

	public ITypeBinding getDeclaringClass() {
		return new TypeBindingBuilder(path, packageName, modifyingParent, null, parentData);
	}

	public Object getDefaultValue() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding[] getExceptionTypes() {
		throw new UnsupportedOperationException();
	}

	public IMethodBinding getMethodDeclaration() {
		throw new UnsupportedOperationException();
	}

	public IAnnotationBinding[] getParameterAnnotations(int arg0) {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding[] getParameterTypes() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getReturnType() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding[] getTypeArguments() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding[] getTypeParameters() {
		throw new UnsupportedOperationException();
	}

	public boolean isAnnotationMember() {
		throw new UnsupportedOperationException();
	}

	public boolean isConstructor() {
		throw new UnsupportedOperationException();
	}

	public boolean isDefaultConstructor() {
		throw new UnsupportedOperationException();
	}

	public boolean isGenericMethod() {
		throw new UnsupportedOperationException();
	}

	public boolean isParameterizedMethod() {
		throw new UnsupportedOperationException();
	}

	public boolean isRawMethod() {
		throw new UnsupportedOperationException();
	}

	public boolean isSubsignature(IMethodBinding arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean isVarargs() {
		throw new UnsupportedOperationException();
	}

	public boolean overrides(IMethodBinding arg0) {
		throw new UnsupportedOperationException();
	}

}