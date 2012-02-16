/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.jdtBinding.mocks;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

public class TypeBindingBuilder extends BindingBuilder implements ITypeBinding {

	protected TypeBindingBuilder(Path path, String packageName, boolean modifyingParent, JavaElementData parentData, JavaElementData childData) {
		super(path, packageName, modifyingParent, parentData, childData);
	}

	@Override
	protected IBindingBuilder newBuilder(JavaElementData data) {
		return newTypeBuilder(data);
	}

	@Override
	public IPackageBinding getPackage() {
		return new PackageBindingAdapter() {

			@Override
			public String getName() {
				return packageName;
			}
		};
	}

	@Override
	public ITypeBinding createArrayType(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getBinaryName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding getBound() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding getComponentType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IVariableBinding[] getDeclaredFields() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IMethodBinding[] getDeclaredMethods() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getDeclaredModifiers() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding[] getDeclaredTypes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding getDeclaringClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IMethodBinding getDeclaringMethod() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getDimensions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding getElementType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding getErasure() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding getGenericTypeOfWildcardType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding[] getInterfaces() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getQualifiedName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getRank() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding getSuperclass() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding[] getTypeArguments() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding[] getTypeBounds() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding getTypeDeclaration() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding[] getTypeParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ITypeBinding getWildcard() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAnnotation() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAnonymous() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAssignmentCompatible(ITypeBinding arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCapture() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCastCompatible(ITypeBinding arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEnum() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFromSource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isGenericType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInterface() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isLocal() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isMember() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNested() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNullType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isParameterizedType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPrimitive() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRawType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSubTypeCompatible(ITypeBinding arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTopLevel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTypeVariable() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUpperbound() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWildcardType() {
		throw new UnsupportedOperationException();
	}

}