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

	@Override
	protected IBindingBuilder newBuilder(JavaElementData data) {
		return newMethodBuilder(data);
	}

	@Override
	public ITypeBinding getDeclaringClass() {
		return new TypeBindingBuilder(path, packageName, modifyingParent, null, parentData);
	}

	@Override
	public Object getDefaultValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding[] getExceptionTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IMethodBinding getMethodDeclaration() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IAnnotationBinding[] getParameterAnnotations(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding[] getParameterTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding getReturnType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding[] getTypeArguments() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding[] getTypeParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAnnotationMember() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isConstructor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDefaultConstructor() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isGenericMethod() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isParameterizedMethod() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRawMethod() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSubsignature(IMethodBinding arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isVarargs() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean overrides(IMethodBinding arg0) {
		throw new UnsupportedOperationException();
	}

}