/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jdtBinding.mocks;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

public class TypeBindingBuilder extends BindingBuilder implements ITypeBinding {

	protected TypeBindingBuilder(Path path, String packageName, boolean modifyingParent, JavaElementData parentData, JavaElementData childData) {
		super(path, packageName, modifyingParent, parentData, childData);
	}

	protected IBindingBuilder newBuilder(JavaElementData data) {
		return newTypeBuilder(data);
	}

	public IPackageBinding getPackage() {
		return new PackageBindingAdapter() {

			public String getName() {
				return packageName;
			}
		};
	}

	public ITypeBinding createArrayType(int arg0) {
		throw new UnsupportedOperationException();
	}

	public String getBinaryName() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getBound() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getComponentType() {
		throw new UnsupportedOperationException();
	}

	public IVariableBinding[] getDeclaredFields() {
		throw new UnsupportedOperationException();
	}

	public IMethodBinding[] getDeclaredMethods() {
		throw new UnsupportedOperationException();
	}

	public int getDeclaredModifiers() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding[] getDeclaredTypes() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getDeclaringClass() {
		throw new UnsupportedOperationException();
	}

	public IMethodBinding getDeclaringMethod() {
		throw new UnsupportedOperationException();
	}

	public int getDimensions() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getElementType() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getErasure() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getGenericTypeOfWildcardType() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding[] getInterfaces() {
		throw new UnsupportedOperationException();
	}

	public String getQualifiedName() {
		throw new UnsupportedOperationException();
	}

	public int getRank() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getSuperclass() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding[] getTypeArguments() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding[] getTypeBounds() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getTypeDeclaration() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding[] getTypeParameters() {
		throw new UnsupportedOperationException();
	}

	public ITypeBinding getWildcard() {
		throw new UnsupportedOperationException();
	}

	public boolean isAnnotation() {
		throw new UnsupportedOperationException();
	}

	public boolean isAnonymous() {
		throw new UnsupportedOperationException();
	}

	public boolean isArray() {
		throw new UnsupportedOperationException();
	}

	public boolean isAssignmentCompatible(ITypeBinding arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean isCapture() {
		throw new UnsupportedOperationException();
	}

	public boolean isCastCompatible(ITypeBinding arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean isClass() {
		throw new UnsupportedOperationException();
	}

	public boolean isEnum() {
		throw new UnsupportedOperationException();
	}

	public boolean isFromSource() {
		throw new UnsupportedOperationException();
	}

	public boolean isGenericType() {
		throw new UnsupportedOperationException();
	}

	public boolean isInterface() {
		throw new UnsupportedOperationException();
	}

	public boolean isLocal() {
		throw new UnsupportedOperationException();
	}

	public boolean isMember() {
		throw new UnsupportedOperationException();
	}

	public boolean isNested() {
		throw new UnsupportedOperationException();
	}

	public boolean isNullType() {
		throw new UnsupportedOperationException();
	}

	public boolean isParameterizedType() {
		throw new UnsupportedOperationException();
	}

	public boolean isPrimitive() {
		throw new UnsupportedOperationException();
	}

	public boolean isRawType() {
		throw new UnsupportedOperationException();
	}

	public boolean isSubTypeCompatible(ITypeBinding arg0) {
		throw new UnsupportedOperationException();
	}

	public boolean isTopLevel() {
		throw new UnsupportedOperationException();
	}

	public boolean isTypeVariable() {
		throw new UnsupportedOperationException();
	}

	public boolean isUpperbound() {
		throw new UnsupportedOperationException();
	}

	public boolean isWildcardType() {
		throw new UnsupportedOperationException();
	}

}